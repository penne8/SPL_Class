import atexit
import sqlite3

from DAOs import _Vaccines, _Suppliers, _Clinics, _Logistics
from DTOs import Vaccine


# The Repository
class _Repository:
    __instance = None

    @staticmethod
    def get_instance():
        if _Repository.__instance is None:
            _Repository()
        return _Repository.__instance

    def __init__(self):
        if _Repository.__instance is None:
            _Repository.__instance = self
            self._conn = sqlite3.connect('database.db')
            self.vaccines = _Vaccines.get_instance(self._conn)
            self.suppliers = _Suppliers.get_instance(self._conn)
            self.clinics = _Clinics.get_instance(self._conn)
            self.logistics = _Logistics.get_instance(self._conn)

            # register that on exit the program will commit and close the database
            atexit.register(self._close_db)

    def _close_db(self):
        self._conn.commit()
        self._conn.close()

    def create_tables(self):
        self._conn.executescript("""
        CREATE TABLE vaccines (
            id                  INTEGER         PRIMARY KEY,
            date                DATE            NOT NULL,
            supplier            INTEGER                 ,
            quantity            INTEGER         NOT NULL,
            
            FOREIGN KEY(supplier)               REFERENCES suppliers(id)
        );

        CREATE TABLE suppliers (
            id                  INTEGER         PRIMARY KEY,
            name                STRING          NOT NULL,
            logistic            INTEGER                 ,
            
            FOREIGN KEY(logistic)               REFERENCES logistics(id)
        );
        
        CREATE TABLE clinics (
            id                  INTEGER         PRIMARY KEY,
            location            STRING          NOT NULL,
            demand              INTEGER         NOT NULL,
            logistic            INTEGER                 ,
            
            FOREIGN KEY(logistic)               REFERENCES logistics(id)
        );

        CREATE TABLE logistics (
            id                  INTEGER         PRIMARY KEY,
            name                STRING          NOT NULL,
            count_sent          INTEGER         NOT NULL,
            count_received      INTEGER         NOT NULL
        );
    """)

    def receive_shipment(self, name, amount, date):
        # Add a vaccine to the vaccines table
        next_vaccine_id = self.vaccines.get_next_id()
        supplier_id = self.suppliers.find_id(name)
        self.vaccines.insert(Vaccine(next_vaccine_id, date, supplier_id, amount))

        # Update the count received on the relevant logistic
        supplier_logistic = self.suppliers.find_logistic(name)
        self.logistics.update_count_received(supplier_logistic, amount)

    def send_shipment(self, location, amount):

        # Reduce the location's demand by the given amount
        self.clinics.update_demand(location, amount)

        # Update the count sent on the relevant logistic
        clinic_logistic = self.clinics.find_logistic(location)
        self.logistics.update_count_sent(clinic_logistic, amount)

        # Reduce the amount of available vaccines
        cursor = self.vaccines.get_vaccines_sorted_by_date()
        curr_vaccine = cursor.fetchone()
        while curr_vaccine is not None and amount > 0:
            curr_quantity = self.vaccines.get_quantity(curr_vaccine[0])
            if curr_quantity <= amount:
                self.vaccines.delete(curr_vaccine[0])
                amount -= curr_quantity
                curr_vaccine = cursor.fetchone()

            else:
                self.vaccines.update_quantity(curr_vaccine[0], amount)
                break

    def summary(self):
        total_inventory = self.vaccines.get_sum_of_quantity()
        total_demand = self.clinics.get_sum_of_demands()
        total_received = self.logistics.get_sum_of_count_received()
        total_sent = self.logistics.get_num_of_count_sent()
        return "{},{},{},{}\n".format(total_inventory, total_demand, total_received, total_sent)
