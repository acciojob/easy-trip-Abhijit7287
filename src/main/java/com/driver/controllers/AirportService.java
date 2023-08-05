package com.driver.controllers;

import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import com.driver.model.Passenger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
 class TripService
{
    @Autowired
    private TripRepository tripRepository=new TripRepository();

    public void addAirPort(Airport airport)
    {
        Map<String,Airport> map=tripRepository.getAirportMap();
        map.put(airport.getAirportName(),airport);
    }

    public String getLargestAirPort()
    {
        Map<String,Airport> map=tripRepository.getAirportMap();
        if(map.size()==0)return "";
        List<String>airList=new ArrayList<>();
        int max=0;
        for(Airport a:map.values())
        {
            if(a.getNoOfTerminals()>max)max=a.getNoOfTerminals();
        }

        for(Airport a:map.values())
        {
            if(a.getNoOfTerminals()==max)airList.add(a.getAirportName());
        }
        return airList.size()==1?airList.get(0):lexographically(airList);
    }

    public String lexographically(List<String>list)
    {
        String pans=list.get(0);
        for(String s:list){
            if(s.compareToIgnoreCase(pans)<0)pans=s;
        }
        return pans;
    }

    public double getShortestDurationOfPossibleBetweenTwoCities(City fromCity,City toCity)
    {
        Map<Integer, Flight>flightMap=tripRepository.getFlightMap();

        //List of the Flights.. flight from
        List<Flight>flights=new ArrayList<>();

        for(Flight flight:flightMap.values())
        {
            if(flight.getFromCity()==fromCity && flight.getToCity()==toCity)
            {
                flights.add(flight);
            }
        }
        if(flights.size()==0)return -1;
        return shortest(flights);
    }

    public double shortest(List<Flight>flights)
    {
        double ans=Double.MAX_VALUE;
        for(Flight f:flights){
            if(f.getDuration()<ans)ans=f.getDuration();
        }
        return ans;
    }

    public String addFlight(Flight flight)
    {
        Map<Integer,Flight> flightMap=tripRepository.getFlightMap();
        flightMap.put(flight.getFlightId(),flight);
        return "SUCCESS";
    }

    public  String bookATicket(Integer flightId,Integer passangerId)
    {

        //If the numberOfPassengers who have booked the flight is greater than : maxCapacity, in that case :
        //return a String "FAILURE"
        //Also if the passenger has already booked a flight then also return "FAILURE".
        //else if you are able to book a ticket then return "SUCCESS"
        Map<Integer,List<Passenger>> flightPassangerMap=tripRepository.getFlightPassangerMap();

        List<Passenger>list=flightPassangerMap.getOrDefault(flightId,new ArrayList<>());

        Map<Integer,Flight>flightMap=tripRepository.getFlightMap();
        if(flightMap.get(flightId).getMaxCapacity()<=list.size())return "FAILURE";


        Map<Integer,List<Flight>>bookedFlightByPassanger=tripRepository.getBookedFlightByPassanger();
        /*
         *It may fail... when passenger has already booked a tiket..
         */
        if(bookedFlightByPassanger.containsKey(passangerId))
        {
            //this means passengers has already booked the ticket..
            //List of the passenger.. for this flight..
            List<Passenger> tempList=flightPassangerMap.get(flightId);
            for(Passenger p:tempList)
            {
                if(passangerId.equals(p.getPassengerId()))return "Failure";
            }
        }


        Map<Integer,Passenger>passengerMap=tripRepository.getPassengerMap();
        list.add(passengerMap.get(passangerId));

        flightPassangerMap.put(flightId,list);

        return "SUCCESS";
    }

    public String cancelATicket(Integer flightId,Integer passengerId)
    {
        //If the passenger has not booked a ticket for that flight or the flightId is invalid or in any other failure case
        // then return a "FAILURE" message
        // Otherwise return a "SUCCESS" message
        // and also cancel the ticket that passenger had booked earlier on the given flightId
        Map<Integer,Flight>flightMap=tripRepository.getFlightMap();
        if(flightMap.containsKey(flightId))return "FAILURE";

        Map<Integer,Passenger>passengerMap=tripRepository.getPassengerMap();
        if(!passengerMap.containsKey(passengerId))return "FAILURE";
        //this shows booked Flight by passanger..
        Map<Integer,List<Flight>>bookedFlightByPassanger=tripRepository.getBookedFlightByPassanger();

        if(!bookedFlightByPassanger.containsKey(passengerId))return "FAILURE";
        List<Flight>flightList= bookedFlightByPassanger.get(passengerId);
        Flight flightToRemove=flightMap.get(flightId);
        flightList.remove(flightToRemove);

        Map<Integer, List<Passenger>>flightPassangerMap=tripRepository.getFlightPassangerMap();

        //I'll have to remove the from the flightPassanger Map..
        List<Passenger>list=flightPassangerMap.get(flightId);
        list.remove(passengerMap.get(passengerId));

        return "SUCCESS";
    }


    public String addPassenger(Passenger passenger){
        Map<Integer,Passenger>passengerMap=tripRepository.getPassengerMap();
        passengerMap.put(passenger.getPassengerId(),passenger);
        return "SUCCESS";
    }

    public int getNumberOfPeopleOn(Date date,String airportName)
    {
        Map<Integer, List<Passenger>>flightPassangerMap=tripRepository.getFlightPassangerMap();
        Map<Integer, Flight>flightMap=tripRepository.getFlightMap();
        Map<String, Airport> airportMap=tripRepository.getAirportMap();

        int count=0;
        for(Integer flightId:flightPassangerMap.keySet())
        {
            if((flightMap.get(flightId).getFromCity().equals(airportMap.get(airportName).getCity()) || flightMap.get(flightId).getToCity().equals(airportMap.get(airportName).getCity())) && !flightMap.get(flightId).getFlightDate().before(date)&& !flightMap.get(flightId).getFlightDate().after(date))
            {
                count++;
            }
        }
        return count;
    }


    public int calculateFare(Integer flightId)
    {
        //Calculation of flight prices is a function of number of people who have booked the flight already.
        //Price for any flight will be : 3000 + noOfPeopleWhoHaveAlreadyBooked*50
        //Suppose if 2 people have booked the flight already : the price of flight for the third person will be 3000 + 2*50 = 3100
        //This will not include the current person who is trying to book, he might also be just checking price
        Map<Integer, List<Passenger>>flightPassangerMap=tripRepository.getFlightPassangerMap();
        int totaFare=3000;
        totaFare+=flightPassangerMap.get(flightId).size()*50;
        return totaFare;
    }

    public int countOfBookingsDoneByPassengerAllCombined(Integer passengerId)
    {
        Map<Integer,List<Flight>>bookedFlightByPassanger=tripRepository.getBookedFlightByPassanger();
        if(bookedFlightByPassanger.containsKey(passengerId)==false)return 0;
        return bookedFlightByPassanger.get(passengerId).size();
    }

    public String getAirportNameFromFlightId(Integer flightId){
        if(flightId==null)return null;

        Flight ans=null;
        Map<Integer, Flight>flightMap=tripRepository.getFlightMap();
        ans=flightMap.get(flightId);

        if(ans==null)return null;
        City city=ans.getFromCity();

        Map<String, Airport> airportMap=tripRepository.getAirportMap();
        for(Airport airport:airportMap.values()){
            if(airport.getCity()==city)return airport.getAirportName();
        }
        return null;
    }


    public int calculateRevenueOfAFlight(Integer flightId)
    {
        //Calculate the total revenue that a flight could have
        //That is of all the passengers that have booked a flight till now and then calculate the revenue
        //Revenue will also decrease if some passenger cancels the flight
        Map<Integer, List<Passenger>>flightPassangerMap=tripRepository.getFlightPassangerMap();
        List<Passenger>passengerList=flightPassangerMap.getOrDefault(flightId,new ArrayList<>());
        if(passengerList.size()==0)return 0;
        int totalRevenue=0;
        for(int i=0;i<passengerList.size();i++)
        {
            totalRevenue+=(3000+i*50);
        }
        return totalRevenue;

    }
}