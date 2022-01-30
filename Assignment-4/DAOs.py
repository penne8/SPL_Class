# Data Access Objects (DAO):
# These objects contain methods for retrieving and storing DTOs.
# Each DAO is responsible for a single DTO.
class _Vaccines:
    __instance = None

    @staticmethod
    def get_instance(conn):
        if _Vaccines.__instance is None:
            _Vaccines(conn)
        return _Vaccines.__instance

    def __init__(self, conn):
        if _Vaccines.__instance is None:
            _Vaccines.__instance = self
            self._conn = conn

    def insert(self, vaccine):
        self._conn.execute("""
               INSERT INTO vaccines (id, date, supplier, quantity) VALUES (?, ?, ?, ?)
           """, [vaccine.id, vaccine.date, vaccine.supplier, vaccine.quantity])

    def get_next_id(self):
        return self._conn.cursor().execute(""" SELECT max(id) FROM vaccines """).fetchone()[0] + 1

    def get_vaccines_sorted_by_date(self):
        return self._conn.cursor().execute(""" SELECT id FROM vaccines ORDER BY DATE(date) """)

    def get_quantity(self, vaccine_id):
        return self._conn.cursor().execute(""" SELECT quantity FROM vaccines WHERE id = (?) """,
                                           [vaccine_id]).fetchone()[0]

    def delete(self, vaccine_id):
        self._conn.execute(""" DELETE FROM vaccines WHERE id = (?) """, [vaccine_id])

    def update_quantity(self, vaccine_id, amount):
        self._conn.execute(""" UPDATE vaccines SET quantity = quantity - (?) WHERE id = (?) """,
                           [amount, vaccine_id])

    def get_sum_of_quantity(self):
        return self._conn.execute(""" SELECT SUM(quantity) FROM vaccines """).fetchone()[0]


class _Suppliers:
    __instance = None

    @staticmethod
    def get_instance(conn):
        if _Suppliers.__instance is None:
            _Suppliers(conn)
        return _Suppliers.__instance

    def __init__(self, conn):
        if _Suppliers.__instance is None:
            _Suppliers.__instance = self
            self._conn = conn

    def insert(self, supplier):
        self._conn.execute("""
               INSERT INTO suppliers (id, name, logistic) VALUES (?, ?, ?)
           """, [supplier.id, supplier.name, supplier.logistic])

    def find_id(self, supplier_name):
        return self._conn.cursor().execute(""" SELECT id FROM suppliers WHERE name = ? """, [supplier_name]).fetchone()[
            0]

    def find_logistic(self, supplier_name):
        return self._conn.cursor().execute(""" SELECT logistic FROM suppliers WHERE name = ? """,
                                           [supplier_name]).fetchone()[0]


class _Clinics:
    __instance = None

    @staticmethod
    def get_instance(conn):
        if _Clinics.__instance is None:
            _Clinics(conn)
        return _Clinics.__instance

    def __init__(self, conn):
        if _Clinics.__instance is None:
            _Clinics.__instance = self
            self._conn = conn

    def insert(self, clinic):
        self._conn.execute("""
               INSERT INTO clinics (id, location, demand, logistic) VALUES (?, ?, ?, ?)
           """, [clinic.id, clinic.location, clinic.demand, clinic.logistic])

    def update_demand(self, location, amount):
        self._conn.execute(""" UPDATE clinics SET demand = demand - (?) WHERE location = (?) """,
                           [amount, location])

    def find_logistic(self, location):
        return self._conn.cursor().execute(""" SELECT logistic FROM clinics WHERE location = ? """,
                                           [location]).fetchone()[0]

    def get_sum_of_demands(self):
        return self._conn.execute(""" SELECT SUM(demand) FROM clinics """).fetchone()[0]


class _Logistics:
    __instance = None

    @staticmethod
    def get_instance(conn):
        if _Logistics.__instance is None:
            _Logistics(conn)
        return _Logistics.__instance

    def __init__(self, conn):
        if _Logistics.__instance is None:
            _Logistics.__instance = self
            self._conn = conn

    def insert(self, logistic):
        self._conn.execute("""
               INSERT INTO logistics (id, name, count_sent, count_received) VALUES (?, ?, ?, ?)
           """, [logistic.id, logistic.name, logistic.count_sent, logistic.count_received])

    def update_count_received(self, logistic, amount):
        self._conn.execute(""" UPDATE logistics SET count_received = count_received + (?) WHERE id = (?) """,
                           [amount, logistic])

    def update_count_sent(self, logistic, amount):
        self._conn.execute(""" UPDATE logistics SET count_sent = count_sent + (?) WHERE id = (?) """,
                           [amount, logistic])

    def get_sum_of_count_received(self):
        return self._conn.execute(""" SELECT SUM(count_received) FROM logistics """).fetchone()[0]

    def get_num_of_count_sent(self):
        return self._conn.execute(""" SELECT SUM(count_sent) FROM logistics """).fetchone()[0]