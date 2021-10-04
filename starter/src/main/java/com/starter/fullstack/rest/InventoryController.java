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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
   * Find Inventory.
   * @param sortVariable variable to sort by
   * @param direction direction to sort in
   * @return List of Inventory.
   */
  @GetMapping("/inventorySorted/")
  public List<Inventory> findSortedInventories(@RequestParam String sortVariable, @RequestParam String direction) {
    return this.inventoryDAO.findSorted(sortVariable, direction);
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
   * update Inventory
   * @param inventory inventory 
   * @return inventory
   */
  @PostMapping(value = "/update")
  public Inventory update(@Valid @RequestBody Inventory inventory) {
    Optional<Inventory> optInv = this.inventoryDAO.update(inventory.getId(), inventory);

    if (optInv.isEmpty()) {
      return null;
    }
    return optInv.get(); 
  }


  /**
   * Retrieve Inventory.
   * @param id Inventory id to Retrieve.
   * @return Found Inventory.
   */
  @GetMapping(value = "/retrieveInventory/")
  @ResponseBody
  public Inventory retrieveInventoryById(@RequestParam String id) {

    Optional<Inventory> optInv = this.inventoryDAO.retrieve(id);

    if (optInv.isEmpty()) {
      return null;
    }
    return optInv.get(); 
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
