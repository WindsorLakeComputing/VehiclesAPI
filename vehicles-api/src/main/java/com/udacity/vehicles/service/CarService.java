package com.udacity.vehicles.service;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;
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
        /**
         * TODO: Add the Maps and Pricing Web Clients you create
         *   in `VehiclesApiApplication` as arguments and set them here.
         */

        this.repository = repository;
        this.maps = maps;
        this.pricing = pricing;
        this.modelMapper = modelMapper;
        priceC = new PriceClient(pricing);
        mapC = new MapsClient(maps, modelMapper);
        Location loc = new Location(20.0, 30.0);
        System.out.println("Inside CarService constructor .. priceC.get() == " + priceC.getPrice(1L));
        Location dest = mapC.getAddress(loc);
        System.out.println("Inside CarService constructor .. mapC.getCity() == " + dest.getCity());
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
        /**
         * TODO: Find the car by ID from the `repository` if it exists.
         *   If it does not exist, throw a CarNotFoundException
         *   Remove the below code as part of your implementation.
         */
        /**
        System.out.println("Inside CarService findById .. priceC.get() == " + priceC.getPrice(2L));
        Location loc = new Location(20.0, 30.0);
        System.out.println("The loc to find is ... loc.getLat() " + loc.getLat());
        Location dest = mapC.getAddress(loc);
        System.out.println("Inside CarService findById .. mapC.getCity() == " + dest.getCity());
         */
        Car carReq = getCarById(id);
        System.out.println("Inside Car findById");
        /**
        for(Car car : list()){
            System.out.println("The car id is " + car.getId());
            if(car.getId() == id){
                carReq = car;

            }
        }
         */
        System.out.println("carReq is " + carReq);
        if(carReq == null){
            throw new CarNotFoundException();
        }


        //Car car = new Car();

        /**
         * TODO: Use the Pricing Web client you create in `VehiclesApiApplication`
         *   to get the price based on the `id` input'
         * TODO: Set the price of the car
         * Note: The car class file uses @transient, meaning you will need to call
         *   the pricing service each time to get the price.
         */

        carReq.setPrice(priceC.getPrice(carReq.getId()));


        /**
         * TODO: Use the Maps Web client you create in `VehiclesApiApplication`
         *   to get the address for the vehicle. You should access the location
         *   from the car object and feed it to the Maps service.
         * TODO: Set the location of the vehicle, including the address information
         * Note: The Location class file also uses @transient for the address,
         * meaning the Maps service needs to be called each time for the address.
         */

        carReq.setLocation(mapC.getAddress(carReq.getLocation()));




        return carReq;
    }

    /**
     *
     *
     */
    public Car getCarById(long id){
        Car carReq = null;
        for(Car car : list()){
            System.out.println("The car id is " + car.getId());
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
                        carToBeUpdated.setLocation(car.getLocation());
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
        /**
         * TODO: Find the car by ID from the `repository` if it exists.
         *   If it does not exist, throw a CarNotFoundException
         */
        Car carReq = getCarById(id);
        if(carReq == null){
            throw new CarNotFoundException();
        }





        /**
         * TODO: Delete the car from the repository.
         */
        repository.delete(carReq);


    }
}
