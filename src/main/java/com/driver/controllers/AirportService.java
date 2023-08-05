package com.driver.controllers;

import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class AirportService {

    @Autowired
    AirportRepository repo;

    ///4
    public String bookflight(int fid , int pid){

        HashMap<Integer,List<Integer>> bookRepo = repo.getBookingRepo();
        HashMap<Integer,List<Integer>> addPass = repo.getNoOfPassengeresInFlight();
        HashMap<Integer,Flight> flightinfo = repo.getFlightInfo();

        Flight f = flightinfo.get(fid);
        List<Integer> l2 = addPass.get(fid);
        List<Integer> l1 = bookRepo.get(pid);

        if(l2.size()==f.getMaxCapacity()){
            return "FAILURE";
        }

        for(int i=0;i<l1.size();i++){
            if(l1.get(i)==fid){
                return "FAILURE";
            }
        }

        ///booking flight for passenger
        l1.add(fid);
        bookRepo.put(pid,l1);

        ///adding passengers on flight
        l2.add(pid);
        addPass.put(fid,l2);

        return "SUCCESS";

    }

    ///5
    public String CancleTicket(int fid , int pid){

        HashMap<Integer,List<Integer>> bookRepo = repo.getBookingRepo();
        HashMap<Integer,List<Integer>> addPass = repo.getNoOfPassengeresInFlight();
        HashMap<Integer,Flight> flightinfo = repo.getFlightInfo();

        ///if flightid is wrong or not present in flightinfo
        if(flightinfo.containsKey(fid)==false){
            return "FAILURE";
        }

        List<Integer> l1 = bookRepo.get(pid);
        int count=0;

        ///checking if that flight is there in passengers booking list
        for(int i=0;i<l1.size();i++){
            if(l1.get(i)==fid){
               count++;
            }
        }
        if(count==0){
            return "FAILURE";
        }

        ///removing from addpass
        List<Integer> l2 = addPass.get(fid);
        for(int i=0;i<l2.size();i++){
            if(l2.get(i)==pid){
              l2.remove(i);
              break;
            }
        }
        addPass.put(fid,l2);

        ///removing flight from passengers list
        for(int i=0;i<l1.size();i++){
            if(l1.get(i)==fid){
               l1.remove(i);
            }
        }
        bookRepo.put(pid,l1);

        return "SUCCESS";
    }

    ///6
    public String getLargestAirport(){

        HashMap<String,Airport> airRepo = repo.getAirportRepo();

        int size = Integer.MIN_VALUE;

        String s = "";

        for(String key : airRepo.keySet()){
            Airport a = airRepo.get(key);

            if(a.getNoOfTerminals()>size){
                s = a.getAirportName();
            }
            else if(a.getNoOfTerminals()==size){

                String str = a.getAirportName();

                int result = str.compareTo(s);

                if(result<0){
                    s = str;
                }
            }
        }

        return s;
    }

    ///7
    public double getShortestTime(City from , City to){

        HashMap<Integer,Flight> flightinfo = repo.getFlightInfo();

        double ans = Integer.MAX_VALUE;

        for(int key : flightinfo.keySet()){
            Flight f = flightinfo.get(key);

            if(f.getFromCity().equals(from) && f.getToCity().equals(to) && f.getDuration()<ans){
                ans = f.getDuration();
            }
        }

        return ans==Integer.MAX_VALUE?-1:ans;
    }

    ///8
    public int getNoOfPeoples(Date d1 , String Airportname){

        HashMap<String,Airport> airRepo = repo.getAirportRepo();
        Airport a1 = airRepo.get(Airportname);

        City c1 = a1.getCity();

        HashMap<Integer,List<Integer>> addPass = repo.getNoOfPassengeresInFlight();
        HashMap<Integer,Flight> flightinfo = repo.getFlightInfo();

        int ans =0;

        for(int key : addPass.keySet()){

            Flight f1  = flightinfo.get(key);

            if(f1.getToCity().equals(c1) || f1.getFromCity().equals(c1)){
                ans += addPass.get(key).size();
            }
        }

        return ans;
    }

    ///9
    public int calculateFare(int fid){

        HashMap<Integer,List<Integer>> addPass = repo.getNoOfPassengeresInFlight();

        int no = addPass.get(fid).size();

        return 3000 + no*50;

    }
    ///10
    public int getCountOfBookings(int pid){

        HashMap<Integer,List<Integer>> bookRepo = repo.getBookingRepo();

        return bookRepo.get(pid).size();
    }

    ///11
    public String getAirportName(int fid){

        HashMap<Integer,Flight> flightinfo = repo.getFlightInfo();
        HashMap<String,Airport> airRepo = repo.getAirportRepo();

        if(flightinfo.containsKey(fid)==false){
            return null;
        }
        Flight f1 = flightinfo.get(fid);

        City c1 = f1.getFromCity();

        for(String key : airRepo.keySet()){

            Airport a1 = airRepo.get(key);

            if(a1.getCity().equals(c1)){
                return a1.getAirportName();
            }
        }

        return null;
    }

    ///12
    public int calculateTotalFare(int fid){

        HashMap<Integer,List<Integer>> addPass = repo.getNoOfPassengeresInFlight();

        List<Integer> list = addPass.get(fid);

        int ans =0;

        for(int i=0;i<list.size();i++){
            ans+=3000+i*50;
        }

        return ans;
    }

}
