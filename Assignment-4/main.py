import sys

from DTOs import *
from Repository import _Repository

# Create a global variable for the repository
repo = _Repository.get_instance()


def prepare_vaccine(vaccine_data):
    id = int(vaccine_data[0])
    date = vaccine_data[1]
    supplier = int(vaccine_data[2])
    quantity = int(vaccine_data[3])
    repo.vaccines.insert(Vaccine(id, date, supplier, quantity))


def prepare_supplier(supplier_data):
    id = int(supplier_data[0])
    name = supplier_data[1]
    logistic = int(supplier_data[2])
    repo.suppliers.insert(Supplier(id, name, logistic))


def prepare_clinic(clinic_data):
    id = int(clinic_data[0])
    location = clinic_data[1]
    demand = int(clinic_data[2])
    logistic = int(clinic_data[3])
    repo.clinics.insert(Clinic(id, location, demand, logistic))


def prepare_logistic(logistic_data):
    id = int(logistic_data[0])
    name = logistic_data[1]
    count_sent = int(logistic_data[2])
    count_received = int(logistic_data[3])
    repo.logistics.insert(Logistic(id, name, count_sent, count_received))


def main(config_path, orders_path, output_path):
    # Create tables
    repo.create_tables()

    # Apply config
    config = open(config_path, "r")
    amounts = config.readline().split(",")
    # remove the "/n" from the end of the line
    amounts[-1] = amounts[-1].strip('\n')
    num_of_vaccines = int(amounts[0])
    num_of_suppliers = int(amounts[1])
    num_of_clinics = int(amounts[2])
    num_of_logistics = int(amounts[3])

    for vaccine in range(num_of_vaccines):
        vaccine_data = config.readline().split(",")
        # remove the "/n" from the end of the line
        vaccine_data[-1] = vaccine_data[-1].strip('\n')
        prepare_vaccine(vaccine_data)

    for supplier in range(num_of_suppliers):
        supplier_data = config.readline().split(",")
        # remove the "/n" from the end of the line
        supplier_data[-1] = supplier_data[-1].strip('\n')
        prepare_supplier(supplier_data)

    for clinic in range(num_of_clinics):
        clinic_data = config.readline().split(",")
        # remove the "/n" from the end of the line
        clinic_data[-1] = clinic_data[-1].strip('\n')
        prepare_clinic(clinic_data)

    for logistic in range(num_of_logistics):
        logistic_data = config.readline().split(",")
        # remove the "/n" from the end of the line
        logistic_data[-1] = logistic_data[-1].strip('\n')
        prepare_logistic(logistic_data)

    # Handle orders
    orders = open(orders_path, "r")
    output = open(output_path, "w")
    for line in orders:
        order = line.split(",")
        order[-1] = order[-1].strip('\n')
        if len(order) == 2:
            repo.send_shipment(order[0], int(order[1]))
        else:
            repo.receive_shipment(order[0], int(order[1]), order[2])
        output.write(repo.summary())

    # Close the file
    output.close()


if __name__ == '__main__':
    main(*sys.argv[1:])
