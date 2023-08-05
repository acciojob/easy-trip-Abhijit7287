package com.driver.controllers;

import com.driver.model.Airport;
import com.driver.model.Flight;
import com.driver.model.Passenger;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class AirportRepository {


    HashMap<String,Airport> AirportRepo = new HashMap<>();

    HashMap<Integer,Flight> FlightInfo = new HashMap<>();

    HashMap<Integer,Passenger> PassengerInfo = new HashMap<>();

    HashMap<Integer,List<Integer>> BookingRepo = new HashMap<>();

    HashMap<Integer,List<Integer>> NoOfPassengeresInFlight = new HashMap<>();


    ///1
    public String addAirport(Airport a1){

        String name = a1.getAirportName();

        AirportRepo.put(name,a1);

        return "SUCCESS";

    }

    ///2
    public String addFlight(Flight f1){

        int a1 = f1.getFlightId();

        FlightInfo.put(a1,f1);
        NoOfPassengeresInFlight.put(a1,new ArrayList<>());

        return "SUCCESS";
    }

    ///3
    public String addPassenger(Passenger p1){

        int a1 = p1.getPassengerId();

        PassengerInfo.put(a1,p1);
        BookingRepo.put(a1,new ArrayList<>());

        return "SUCCESS";
    }


    public HashMap<String, Airport> getAirportRepo() {
        return AirportRepo;
    }

    public HashMap<Integer, Flight> getFlightInfo() {
        return FlightInfo;
    }

    public HashMap<Integer, Passenger> getPassengerInfo() {
        return PassengerInfo;
    }

    public HashMap<Integer, List<Integer>> getBookingRepo() {
        return BookingRepo;
    }

    public HashMap<Integer, List<Integer>> getNoOfPassengeresInFlight() {
        return NoOfPassengeresInFlight;
    }
}
