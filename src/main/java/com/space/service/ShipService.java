package com.space.service;

import com.space.Exceptions.BadRequestExeption;
import com.space.Exceptions.NotFoundException;
import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ShipService {
    private final ShipRepository shipRepository;

    @Autowired
    public ShipService(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }


    public List<Ship> getFilteredShipList(String name, String planet, ShipType shipType, Long after, Long before,
                                  Boolean getUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize,
                                  Integer maxCrewSize, Double minRating, Double maxRating) {
        List<Ship> Ships = shipRepository.findAll();
        List<Ship> result = new ArrayList<>();

        for (Ship ship:Ships) {

            if (name != null        && !ship.getName().contains(name) ) continue;
            if (planet != null      && !ship.getPlanet().contains(planet) ) continue;
            if (shipType != null    && !ship.getShipType().equals(shipType) ) continue;
            if (after != null       && !ship.getProdDate().after(new Date(after)) ) continue;
            if (before != null      && !ship.getProdDate().before(new Date(before)) ) continue;
            if (getUsed != null     && !ship.getUsed() == getUsed ) continue;
            if (minSpeed != null    && ship.getSpeed() < minSpeed ) continue;
            if (maxSpeed != null    && ship.getSpeed() > maxSpeed ) continue;
            if (minCrewSize != null && ship.getCrewSize() < minCrewSize ) continue;
            if (maxCrewSize != null && ship.getCrewSize() > maxCrewSize ) continue;
            if (minRating != null   && ship.getRating() < minRating ) continue;
            if (maxRating != null   && ship.getRating() > maxRating ) continue;
            result.add(ship);
        }
        return result;
    }

    public List<Ship> sortShips(final List<Ship> shipList, ShipOrder order, Integer pageNumber, Integer pageSize) {
        pageNumber = pageNumber == null ? 0 : pageNumber;
        pageSize = pageSize == null ? 3 : pageSize;

        Comparator<Ship> comparator = null;

        if (order == null)
            comparator = Comparator.comparing(Ship::getId);
            else
                switch (order.getFieldName()) {
                    case "speed":
                        comparator = Comparator.comparing(Ship::getSpeed);
                        break;
                    case "prodDate":
                        comparator = Comparator.comparing(Ship::getProdDate);
                        break;
                    case "rating":
                        comparator = Comparator.comparing(Ship::getRating);
                        break;
                    case "id":
                        comparator = Comparator.comparing(Ship::getId);
                        break;
                }


        shipList.sort(comparator);

        int first = pageNumber*pageSize;
        int last = (pageNumber+1)*pageSize;
        last = Math.min(last, shipList.size());

        List<Ship> result = new ArrayList<>();

        for( int i= first;i<last;i++){
            result.add(shipList.get(i));
        }
        return result;

    }

    public Ship getShipById(String id){
        Long l = getValidId(id);

        if (!shipRepository.existsById(l)) throw new NotFoundException();

        return shipRepository.findById(l).orElse(null);

    }

    public Long getValidId(String id){
        long l;
        try {
            l = Long.parseLong(id);
        } catch (Exception e){
            throw  new BadRequestExeption();
        }
        if(l < 1)  throw new BadRequestExeption();
        return l;
    }


    public Ship updateShip (Ship newShip,String id){

        Ship oldShip = getShipById(id);

        if (newShip.getName() != null ) {
            if (newShip.getName().equals("") || newShip.getName().length()>50) throw new BadRequestExeption();
            oldShip.setName(newShip.getName());
        }

        if (newShip.getPlanet() != null) {
            if (newShip.getPlanet().length()>50) throw new BadRequestExeption();
            oldShip.setPlanet(newShip.getPlanet());
        }

        if (newShip.getShipType() != null){
            oldShip.setShipType(newShip.getShipType());
        }

        if (newShip.getProdDate() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(newShip.getProdDate());
            int year = calendar.get(Calendar.YEAR);
            if (2800>year || year >3019) throw new BadRequestExeption();
           oldShip.setProdDate(newShip.getProdDate());
        }

        if (newShip.getUsed() != null){
            oldShip.setUsed(newShip.getUsed());
        }

        if (newShip.getSpeed() != null){
            oldShip.setSpeed(newShip.getSpeed());
        }

        if (newShip.getCrewSize() != null) {
            if (newShip.getCrewSize() < 1 || newShip.getCrewSize() > 9999) throw new BadRequestExeption();
            oldShip.setCrewSize(newShip.getCrewSize());
        }
        oldShip.setRating(calculateRating(oldShip));
        return oldShip;
    }

    public double calculateRating(Ship ship){
        double v = ship.getSpeed();
        double k = ship.getUsed()? 0.5 : 1;
        double y0 = 3019;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ship.getProdDate());

        double y1 = calendar.get(Calendar.YEAR);

        double rating = (80 * v * k) / (y0 - y1 + 1);

        return (double) Math.round(rating * 100) / 100;
    }

    public Ship createShip (Ship ship){
        Ship newShip = new Ship();

        if (ship.getName() != null) {
            if (ship.getName().equals("") || ship.getName().length()>50) throw new BadRequestExeption();
            newShip.setName(ship.getName());
        } else throw new BadRequestExeption();

        if (ship.getPlanet() != null) {
            if (ship.getPlanet().length()>50) throw new BadRequestExeption();
            newShip.setPlanet(ship.getPlanet());
        } else throw new BadRequestExeption();

        if (ship.getShipType() != null){
            newShip.setShipType(ship.getShipType());
        } else throw new BadRequestExeption();

        if (ship.getProdDate() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(ship.getProdDate());
            int year = calendar.get(Calendar.YEAR);
            if (2800>year || year >3019) throw new BadRequestExeption();
            newShip.setProdDate(ship.getProdDate());
        } else throw new BadRequestExeption();

        if (ship.getUsed() != null){
            newShip.setUsed(ship.getUsed());
        } else  newShip.setUsed(false);

        if (ship.getSpeed() != null){
            newShip.setSpeed(ship.getSpeed());
        } else throw new BadRequestExeption();

        if (ship.getCrewSize() != null) {
            if (ship.getCrewSize() < 1 || ship.getCrewSize() > 9999) throw new BadRequestExeption();
            newShip.setCrewSize(ship.getCrewSize());
        } else throw new BadRequestExeption();

        newShip.setRating(calculateRating(newShip));

        return shipRepository.save(newShip);

    }

    public void deleteShip (String id){
        Long l = getValidId(id);
        if (!shipRepository.existsById(l)) throw new NotFoundException();
        else shipRepository.deleteById(l);
    }
}
