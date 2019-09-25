package com.udacity.vehicles.service;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;

import java.time.LocalDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Implements the car service create, read, update or delete
 * information about vehicles, as well as gather related
 * location and price data when desired.
 */
@Service
public class CarService {

    private final CarRepository repository;
    private WebClient maps;
    private WebClient pricing;
    private PriceClient priceC;
    private MapsClient mapC;
    private ModelMapper modelMapper;

    @Autowired
    public CarService(CarRepository repository, WebClient maps, WebClient pricing, ModelMapper modelMapper) {
        this.repository = repository;
        this.maps = maps;
        this.pricing = pricing;
        this.modelMapper = modelMapper;
        priceC = new PriceClient(pricing);
        mapC = new MapsClient(maps, modelMapper);
    }

    /**
     * Gathers a list of all vehicles
     * @return a list of all vehicles in the CarRepository
     */
    public List<Car> list() {
        return repository.findAll();
    }

    /**
     * Gets car information by ID (or throws exception if non-existent)
     * @param id the ID number of the car to gather information on
     * @return the requested car's information, including location and price
     */
    public Car findById(Long id) {
        Car carReq = getCarById(id);
        if(carReq == null){
            throw new CarNotFoundException();
        }
        carReq.setPrice(priceC.getPrice(carReq.getId()));
        carReq.setLocation(mapC.getAddress(carReq.getLocation()));

        return carReq;
    }

    /**
     * Helper function to retrieve a car via its id
     * @param id the ID number of the car
     */
    public Car getCarById(long id){
        Car carReq = null;
        for(Car car : list()){
            if(car.getId() == id){
                carReq = car;
            }
        }

        return carReq;
    }
    /**
     * Either creates or updates a vehicle, based on prior existence of car
     * @param car A car object, which can be either new or existing
     * @return the new/updated car is stored in the repository
     */
    public Car save(Car car) {
        if (car.getId() != null) {
            return repository.findById(car.getId())
                    .map(carToBeUpdated -> {
                        carToBeUpdated.setDetails(car.getDetails());
                        carToBeUpdated.setLocation(mapC.getAddress(car.getLocation()));
                        carToBeUpdated.setPrice(priceC.getPrice(car.getId()));
                        carToBeUpdated.setModifiedAt(LocalDateTime.now());
                        return repository.save(carToBeUpdated);
                    }).orElseThrow(CarNotFoundException::new);
        }

        return repository.save(car);
    }

    /**
     * Deletes a given car by ID
     * @param id the ID number of the car to delete
     */
    public void delete(Long id) {
        Car carReq = getCarById(id);
        if(carReq == null){
            throw new CarNotFoundException();
        }
        repository.delete(carReq);

    }
}