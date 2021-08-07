package com.starter.fullstack.rest;

import com.starter.fullstack.api.Inventory;
import com.starter.fullstack.dao.InventoryDAO;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Inventory Controller.
 */
@RestController
public class InventoryController {
  private final InventoryDAO inventoryDAO;

  /**
   * Default Constructor.
   * @param inventoryDAO inventoryDAO.
   */
  public InventoryController(InventoryDAO inventoryDAO) {
    Assert.notNull(inventoryDAO, "Inventory DAO must not be null.");
    this.inventoryDAO = inventoryDAO;
  }

  /**
   * Find Inventory.
   * @return List of Inventory.
   */
  @GetMapping("/inventory")
  public List<Inventory> findInventories() {
    return this.inventoryDAO.findAll();
  }


  /**
   * Create Inventory
   * @param inventory inventory 
   * @return inventory
   */
  @PostMapping(value = "/inventory")
  public Inventory create(@Valid @RequestBody Inventory inventory) {
    return this.inventoryDAO.create(inventory);
  }


  /**
   * Retrieve Inventory.
   * @param id Inventory id to Retrieve.
   * @return Found Inventory.
   */
  @GetMapping("/retrieveInventory")
  public Inventory retrieveInventoryById() {
    System.out.println("In inventoryController retrieve by id with id: ");

    /*Optional<Inventory> optInv = this.inventoryDAO.retrieve(id);

    if (optInv.isEmpty()) {
      return null;
    }

    return optInv.get();  */
    return null;
  }

 /**
   * Delete Inventory By Id.
   *
   * @param id String
   * @return Inventory
   */
  @DeleteMapping(value = "/inventory")
  public Inventory deleteInventoryById(@RequestBody List<String> id) {
    Optional<Inventory> optInv = this.inventoryDAO.delete(id);
    if (optInv.isEmpty()) {
      return null;
    }

    return optInv.get();
  } 
}
