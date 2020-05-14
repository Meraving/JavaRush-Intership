package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@ResponseBody
@RequestMapping("/rest")

public class ShipController {

    private final ShipService shipService;

    @Autowired
    public ShipController(ShipService shipService) {
        this.shipService = shipService;
    }

    @GetMapping("/ships")
    public List<Ship> getShipList(@RequestParam(required = false) String name,
                                  @RequestParam(required = false) String planet,
                                  @RequestParam(required = false) ShipType shipType,
                                  @RequestParam(required = false) Long after,
                                  @RequestParam(required = false) Long before,
                                  @RequestParam(required = false) Boolean isUsed,
                                  @RequestParam(required = false) Double minSpeed,
                                  @RequestParam(required = false) Double maxSpeed,
                                  @RequestParam(required = false) Integer minCrewSize,
                                  @RequestParam(required = false) Integer maxCrewSize,
                                  @RequestParam(required = false) Double minRating,
                                  @RequestParam(required = false) Double maxRating,
                                  @RequestParam(required = false) ShipOrder order,
                                  @RequestParam(required = false) Integer pageNumber,
                                  @RequestParam(required = false) Integer pageSize) {
        List<Ship> filteredShips = shipService.getFilteredShipList(name, planet, shipType, after, before, isUsed, minSpeed,
                maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);
        return shipService.sortShips(filteredShips, order, pageNumber, pageSize);
    }

    @GetMapping("/ships/count")
    public Integer getShipCount(@RequestParam(required = false) String name,
                                @RequestParam(required = false) String planet,
                                @RequestParam(required = false) ShipType shipType,
                                @RequestParam(required = false) Long after,
                                @RequestParam(required = false) Long before,
                                @RequestParam(required = false) Boolean isUsed,
                                @RequestParam(required = false) Double minSpeed,
                                @RequestParam(required = false) Double maxSpeed,
                                @RequestParam(required = false) Integer minCrewSize,
                                @RequestParam(required = false) Integer maxCrewSize,
                                @RequestParam(required = false) Double minRating,
                                @RequestParam(required = false) Double maxRating) {
        List<Ship> filteredShips = shipService.getFilteredShipList(name, planet, shipType, after, before, isUsed, minSpeed,
                maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);
        return filteredShips.size();
    }


    @GetMapping("/ships/{id}")
    public Ship getShipById(@PathVariable String id){
        return shipService.getShipById(id);
    }

    @PostMapping ("/ships/{id}")
    public Ship updateShip(@RequestBody Ship ship,@PathVariable String id){
        return shipService.updateShip(ship,id);
    }

    @PostMapping ("/ships")
    public Ship createShip(@RequestBody Ship ship){
        return shipService.createShip(ship);
    }

    @DeleteMapping("/ships/{id}")
    public void deleteShip(@PathVariable String id){
        shipService.deleteShip(id);
    }

}
