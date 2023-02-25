package com.driver.services.impl;

import com.driver.model.TripBooking;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.model.Customer;
import com.driver.model.Driver;
import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;
import com.driver.model.TripStatus;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		customerRepository2.save(customer);
		//Save the customer in database
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		Customer customer = customerRepository2.findById(customerId).get();
		customerRepository2.delete(customer);
		// Delete customer without using deleteById function

	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query

        List<Driver> drivers = driverRepository2.findAll();
		Driver bookedriver = null;
		for(Driver driver : drivers){
			if(driver.getCab().isAvailable()){
				if(bookedriver == null){
					bookedriver = driver;
				}
				if(bookedriver.getDriverId()> driver.getDriverId()){
					bookedriver = driver;
				}
			}
		}
		if(bookedriver == null)
			throw new Exception("No cab available!");

		TripBooking tripBooking = new TripBooking();
		Customer customer = customerRepository2.findById(customerId).get();
		tripBooking.setCustomer(customer);
		tripBooking.setFromLocation(fromLocation);
		tripBooking.setToLocation(toLocation);
		tripBooking.setBill(bookedriver.getCab().getPerKmRate()*distanceInKm);
		tripBooking.setStatus(TripStatus.CONFIRMED);
		tripBooking.setDriver(bookedriver);

		bookedriver.getTripBookingList().add(tripBooking);
		customer.getTripBookingList().add(tripBooking);

		driverRepository2.save(bookedriver);
		tripBookingRepository2.save(tripBooking);
		customerRepository2.save(customer);
		 return tripBooking;


	}

	@Override
	public void cancelTrip(Integer tripId){
		TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
		tripBooking.setStatus(TripStatus.CANCELED);
		tripBooking.setBill(0);
		tripBooking.getDriver().getCab().setAvailable(true);
		tripBookingRepository2.save(tripBooking);
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly

	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
		tripBooking.setStatus(TripStatus.COMPLETED);
		tripBooking.getDriver().getCab().setAvailable(true);
		tripBookingRepository2.save(tripBooking);

	}
}
