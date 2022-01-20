package com.starter.fullstack.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starter.fullstack.api.Inventory;
import com.starter.fullstack.api.UnitOfMeasurement;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class InventoryControllerTest {

  private Instant testDate = Instant.now().truncatedTo(ChronoUnit.DAYS);

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  private Inventory inventory;

  @Before
  public void setup() throws Throwable {
    this.inventory = new Inventory();
    this.inventory.setId(null);
    this.inventory.setName("TEST");
    this.inventory.setUnitOfMeasurement(UnitOfMeasurement.CUP);
    this.inventory.setAmount(BigDecimal.valueOf(1));
    this.inventory.setBestBeforeDate(testDate);
    // Sets the Mongo ID for us
    this.inventory = this.mongoTemplate.save(this.inventory);
  }

  @After
  public void teardown() {
    this.mongoTemplate.dropCollection(Inventory.class);
  }


  /**
   * Test create endpoint.
   * @throws Throwable see MockMvc
   */
  @Test
  public void create() throws Throwable {
    // Create inventory objet to test with
    this.inventory = new Inventory();
    this.inventory.setId(null);
    this.inventory.setName("ALSO TEST");
    this.inventory.setProductType("Beer");

    // Post the inventory object
    this.mockMvc.perform(post("/inventory")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(this.objectMapper.writeValueAsString(this.inventory)))
      .andExpect(status().isOk());

    // test to make sure that the inventory object was posted
    Assert.assertEquals(2, this.mongoTemplate.findAll(Inventory.class).size());
  }

 /**
   * Test delete endpoint.
   * @throws Throwable see MockMvc
   */
  @Test
  public void remove() throws Throwable { 
    Assert.assertEquals(1, this.mongoTemplate.findAll(Inventory.class).size());

    List<String> idNumber = Arrays.asList(inventory.getId());

    this.mockMvc.perform(delete("/inventory")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content("[\"" + this.inventory.getId() + "\"]"))
      .andExpect(status().isOk());

    Assert.assertEquals(0, this.mongoTemplate.findAll(Inventory.class).size());
  }



   /**
   * Test findSortedInventories
   * @throws Throwable see MockMvc
   */
  @Test
  public void findSortedInventories() throws Throwable {
    
    this.mockMvc.perform(get("/filterRetrieve/")
      .param("quantity", "1"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().json("[" + this.objectMapper.writeValueAsString(inventory) + "]"));


    this.mockMvc.perform(get("/filterRetrieve/")
      .param("unitOfMeasure", "CUP"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().json("[" + this.objectMapper.writeValueAsString(inventory) + "]"));


    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.from(ZoneOffset.UTC));



    String date = DateTimeFormatter.ISO_INSTANT.format(testDate);


    this.mockMvc.perform(get("/filterRetrieve/")
      .param("bestBefore", "2022-01-18T00:00:00Z"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().json("[" + this.objectMapper.writeValueAsString(inventory) + "]"));


  }



}

