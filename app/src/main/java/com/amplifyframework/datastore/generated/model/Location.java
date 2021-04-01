package com.amplifyframework.datastore.generated.model;


import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the Location type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Locations")
public final class Location implements Model {
  public static final QueryField ID = field("Location", "id");
  public static final QueryField ADDRESS = field("Location", "address");
  public static final QueryField CITY = field("Location", "city");
  public static final QueryField LAT = field("Location", "lat");
  public static final QueryField LON = field("Location", "lon");
  public static final QueryField REGION = field("Location", "region");
  public static final QueryField STORE_NUMBER = field("Location", "storeNumber");
  public static final QueryField POSTAL_CODE = field("Location", "postalCode");
  public static final QueryField COUNTRY = field("Location", "country");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String") String address;
  private final @ModelField(targetType="String") String city;
  private final @ModelField(targetType="Float") Double lat;
  private final @ModelField(targetType="Float") Double lon;
  private final @ModelField(targetType="String") String region;
  private final @ModelField(targetType="String") String storeNumber;
  private final @ModelField(targetType="String") String postalCode;
  private final @ModelField(targetType="String") String country;
  public String getId() {
      return id;
  }
  
  public String getAddress() {
      return address;
  }
  
  public String getCity() {
      return city;
  }
  
  public Double getLat() {
      return lat;
  }
  
  public Double getLon() {
      return lon;
  }
  
  public String getRegion() {
      return region;
  }
  
  public String getStoreNumber() {
      return storeNumber;
  }
  
  public String getPostalCode() {
      return postalCode;
  }
  
  public String getCountry() {
      return country;
  }
  
  private Location(String id, String address, String city, Double lat, Double lon, String region, String storeNumber, String postalCode, String country) {
    this.id = id;
    this.address = address;
    this.city = city;
    this.lat = lat;
    this.lon = lon;
    this.region = region;
    this.storeNumber = storeNumber;
    this.postalCode = postalCode;
    this.country = country;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Location location = (Location) obj;
      return ObjectsCompat.equals(getId(), location.getId()) &&
              ObjectsCompat.equals(getAddress(), location.getAddress()) &&
              ObjectsCompat.equals(getCity(), location.getCity()) &&
              ObjectsCompat.equals(getLat(), location.getLat()) &&
              ObjectsCompat.equals(getLon(), location.getLon()) &&
              ObjectsCompat.equals(getRegion(), location.getRegion()) &&
              ObjectsCompat.equals(getStoreNumber(), location.getStoreNumber()) &&
              ObjectsCompat.equals(getPostalCode(), location.getPostalCode()) &&
              ObjectsCompat.equals(getCountry(), location.getCountry());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getAddress())
      .append(getCity())
      .append(getLat())
      .append(getLon())
      .append(getRegion())
      .append(getStoreNumber())
      .append(getPostalCode())
      .append(getCountry())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Location {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("address=" + String.valueOf(getAddress()) + ", ")
      .append("city=" + String.valueOf(getCity()) + ", ")
      .append("lat=" + String.valueOf(getLat()) + ", ")
      .append("lon=" + String.valueOf(getLon()) + ", ")
      .append("region=" + String.valueOf(getRegion()) + ", ")
      .append("storeNumber=" + String.valueOf(getStoreNumber()) + ", ")
      .append("postalCode=" + String.valueOf(getPostalCode()) + ", ")
      .append("country=" + String.valueOf(getCountry()))
      .append("}")
      .toString();
  }
  
  public static BuildStep builder() {
      return new Builder();
  }
  
  /** 
   * WARNING: This method should not be used to build an instance of this object for a CREATE mutation.
   * This is a convenience method to return an instance of the object with only its ID populated
   * to be used in the context of a parameter in a delete mutation or referencing a foreign key
   * in a relationship.
   * @param id the id of the existing item this instance will represent
   * @return an instance of this model with only ID populated
   * @throws IllegalArgumentException Checks that ID is in the proper format
   */
  public static Location justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new Location(
      id,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      address,
      city,
      lat,
      lon,
      region,
      storeNumber,
      postalCode,
      country);
  }
  public interface BuildStep {
    Location build();
    BuildStep id(String id) throws IllegalArgumentException;
    BuildStep address(String address);
    BuildStep city(String city);
    BuildStep lat(Double lat);
    BuildStep lon(Double lon);
    BuildStep region(String region);
    BuildStep storeNumber(String storeNumber);
    BuildStep postalCode(String postalCode);
    BuildStep country(String country);
  }
  

  public static class Builder implements BuildStep {
    private String id;
    private String address;
    private String city;
    private Double lat;
    private Double lon;
    private String region;
    private String storeNumber;
    private String postalCode;
    private String country;
    @Override
     public Location build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Location(
          id,
          address,
          city,
          lat,
          lon,
          region,
          storeNumber,
          postalCode,
          country);
    }
    
    @Override
     public BuildStep address(String address) {
        this.address = address;
        return this;
    }
    
    @Override
     public BuildStep city(String city) {
        this.city = city;
        return this;
    }
    
    @Override
     public BuildStep lat(Double lat) {
        this.lat = lat;
        return this;
    }
    
    @Override
     public BuildStep lon(Double lon) {
        this.lon = lon;
        return this;
    }
    
    @Override
     public BuildStep region(String region) {
        this.region = region;
        return this;
    }
    
    @Override
     public BuildStep storeNumber(String storeNumber) {
        this.storeNumber = storeNumber;
        return this;
    }
    
    @Override
     public BuildStep postalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }
    
    @Override
     public BuildStep country(String country) {
        this.country = country;
        return this;
    }
    
    /** 
     * WARNING: Do not set ID when creating a new object. Leave this blank and one will be auto generated for you.
     * This should only be set when referring to an already existing object.
     * @param id id
     * @return Current Builder instance, for fluent method chaining
     * @throws IllegalArgumentException Checks that ID is in the proper format
     */
    public BuildStep id(String id) throws IllegalArgumentException {
        this.id = id;
        
        try {
            UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
        } catch (Exception exception) {
          throw new IllegalArgumentException("Model IDs must be unique in the format of UUID.",
                    exception);
        }
        
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String id, String address, String city, Double lat, Double lon, String region, String storeNumber, String postalCode, String country) {
      super.id(id);
      super.address(address)
        .city(city)
        .lat(lat)
        .lon(lon)
        .region(region)
        .storeNumber(storeNumber)
        .postalCode(postalCode)
        .country(country);
    }
    
    @Override
     public CopyOfBuilder address(String address) {
      return (CopyOfBuilder) super.address(address);
    }
    
    @Override
     public CopyOfBuilder city(String city) {
      return (CopyOfBuilder) super.city(city);
    }
    
    @Override
     public CopyOfBuilder lat(Double lat) {
      return (CopyOfBuilder) super.lat(lat);
    }
    
    @Override
     public CopyOfBuilder lon(Double lon) {
      return (CopyOfBuilder) super.lon(lon);
    }
    
    @Override
     public CopyOfBuilder region(String region) {
      return (CopyOfBuilder) super.region(region);
    }
    
    @Override
     public CopyOfBuilder storeNumber(String storeNumber) {
      return (CopyOfBuilder) super.storeNumber(storeNumber);
    }
    
    @Override
     public CopyOfBuilder postalCode(String postalCode) {
      return (CopyOfBuilder) super.postalCode(postalCode);
    }
    
    @Override
     public CopyOfBuilder country(String country) {
      return (CopyOfBuilder) super.country(country);
    }
  }
  
}
