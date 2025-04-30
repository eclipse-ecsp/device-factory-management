/*
 *  *******************************************************************************
 *  Copyright (c) 2023-24 Harman International
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  SPDX-License-Identifier: Apache-2.0
 *  *******************************************************************************
 */

package org.eclipse.ecsp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.springframework.stereotype.Component;

/**
 * Represents the attributes of a device.
 * This class provides getters and setters for each attribute, as well as constructors for creating new instances.
 * The attributes include the harmanId, country, manufacturer, make, model, year, bodytype, series, vehicletype, and
 * lastlogintime of the device.
 */
@Component("hcp-db-shared.DeviceAttributes")
@JsonInclude(Include.NON_NULL)
public class DeviceAttributes {

    private String harmanId;
    private String country;
    private String manufacturer;
    private String make;
    private String model;
    private int year;
    private String bodytype;
    private String series;
    private String vehicletype;
    private String lastlogintime;

    /**
     * Constructs a new DeviceAttributes object with the specified attributes.
     *
     * @param harmanId       the harmanId of the device
     * @param country        the country of the device
     * @param manufacturer   the manufacturer of the device
     * @param make           the make of the device
     * @param model          the model of the device
     * @param year           the year of the device
     * @param bodytype       the bodytype of the device
     * @param series         the series of the device
     * @param vehicletype    the vehicletype of the device
     * @param lastlogintime  the lastlogintime of the device
     */
    public DeviceAttributes(String harmanId, String country, String manufacturer,
                            String make, String model, int year, String bodytype,
                            String series, String vehicletype, String lastlogintime) {
        super();
        this.harmanId = harmanId;
        this.country = country;
        this.manufacturer = manufacturer;
        this.make = make;
        this.model = model;
        this.year = year;
        this.bodytype = bodytype;
        this.series = series;
        this.vehicletype = vehicletype;
        this.lastlogintime = lastlogintime;
    }

    /**
     * Constructs a new empty DeviceAttributes object.
     */
    public DeviceAttributes() {
        super();
    }

    /**
     * Returns the harmanId of the device.
     *
     * @return The harmanId of the device.
     */
    public String getHarmanId() {
        return harmanId;
    }

    /**
     * Sets the harmanId of the device.
     *
     * @param harmanId The harmanId of the device.
     */
    public void setHarmanId(String harmanId) {
        this.harmanId = harmanId;
    }

    /**
     * Returns the country of the device.
     *
     * @return The country of the device.
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the country of the device.
     *
     * @param country The country of the device.
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Returns the manufacturer of the device.
     *
     * @return The manufacturer of the device.
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * Sets the manufacturer of the device.
     *
     * @param manufacturer The manufacturer of the device.
     */
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    /**
     * Returns the make of the device.
     *
     * @return The make of the device.
     */
    public String getMake() {
        return make;
    }

    /**
     * Sets the make of the device.
     *
     * @param make The make of the device.
     */
    public void setMake(String make) {
        this.make = make;
    }

    /**
     * Returns the model of the device.
     *
     * @return The model of the device.
     */
    public String getModel() {
        return model;
    }

    /**
     * Sets the model of the device.
     *
     * @param model The model of the device.
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Returns the year of the device.
     *
     * @return The year of the device.
     */
    public int getYear() {
        return year;
    }

    /**
     * Sets the year of the device.
     *
     * @param year The year of the device.
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * Returns the bodytype of the device.
     *
     * @return The bodytype of the device.
     */
    public String getBodytype() {
        return bodytype;
    }

    /**
     * Sets the bodytype of the device.
     *
     * @param bodytype The bodytype of the device.
     */
    public void setBodytype(String bodytype) {
        this.bodytype = bodytype;
    }

    /**
     * Returns the series of the device.
     *
     * @return The series of the device.
     */
    public String getSeries() {
        return series;
    }

    /**
     * Sets the series of the device.
     *
     * @param series The series of the device.
     */
    public void setSeries(String series) {
        this.series = series;
    }

    /**
     * Returns the vehicletype of the device.
     *
     * @return The vehicletype of the device.
     */
    public String getVehicletype() {
        return vehicletype;
    }

    /**
     * Sets the vehicletype of the device.
     *
     * @param vehicletype The vehicletype of the device.
     */
    public void setVehicletype(String vehicletype) {
        this.vehicletype = vehicletype;
    }

    /**
     * Returns the lastlogintime of the device.
     *
     * @return The lastlogintime of the device.
     */
    public String getLastlogintime() {
        return lastlogintime;
    }

    /**
     * Sets the lastlogintime of the device.
     *
     * @param lastlogintime The lastlogintime of the device.
     */
    public void setLastlogintime(String lastlogintime) {
        this.lastlogintime = lastlogintime;
    }

    /**
     * Returns a hash code value for the object. This method is used by the Java
     * hashing algorithms when storing objects in hash-based data structures such
     * as HashMap.
     *
     * @return the hash code value for the object
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + ((bodytype == null) ? 0 : bodytype.hashCode());
        result = prime * result
            + ((country == null) ? 0 : country.hashCode());
        result = prime * result
            + ((lastlogintime == null) ? 0 : lastlogintime.hashCode());
        result = prime * result + ((make == null) ? 0 : make.hashCode());
        result = prime * result
            + ((manufacturer == null) ? 0 : manufacturer.hashCode());
        result = prime * result + ((model == null) ? 0 : model.hashCode());
        result = prime * result
            + ((series == null) ? 0 : series.hashCode());
        result = prime * result
            + ((vehicletype == null) ? 0 : vehicletype.hashCode());
        result = prime * result + year;
        result = prime * result
            + ((harmanId == null) ? 0 : harmanId.hashCode());
        return result;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare
     * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DeviceAttributes other = (DeviceAttributes) obj;
        if (verifyBodyType(other)) {
            return false;
        }
        if (verifyCountry(other)) {
            return false;
        }
        if (verifyLastLogin(other)) {
            return false;
        }
        if (verifyMake(other)) {
            return false;
        }
        if (verifyManufacturer(other)) {
            return false;
        }
        if (verifyModel(other)) {
            return false;
        }
        if (verifySeries(other)) {
            return false;
        }
        if (verifyVehicletype(other)) {
            return false;
        }
        if (year != other.year) {
            return false;
        }
        return !verifyHarmanId(other);
    }

    /**
     * Verifies the body type of the device attributes.
     *
     * @param other the other DeviceAttributes object to compare with
     * @return true if the body type is different or one of the body types is null, false otherwise
     */
    private boolean verifyBodyType(DeviceAttributes other) {
        if (bodytype == null) {
            if (other.bodytype != null) {
                return true;
            }
        } else if (!bodytype.equals(other.bodytype)) {
            return true;
        }
        return false;
    }

    /**
     * Verifies if the country of this device attributes object is the same as the country of another device attributes
     * object.
     *
     * @param other the other device attributes object to compare with
     * @return true if the countries are different, false otherwise
     */
    private boolean verifyCountry(DeviceAttributes other) {
        if (country == null) {
            if (other.country != null) {
                return true;
            }
        } else if (!country.equals(other.country)) {
            return true;
        }
        return false;
    }

    /**
     * Verifies if the lastlogintime of this DeviceAttributes object is different from the lastlogintime of another
     * DeviceAttributes object.
     *
     * @param other The other DeviceAttributes object to compare with.
     * @return true if the lastlogintime is different, false otherwise.
     */
    private boolean verifyLastLogin(DeviceAttributes other) {
        if (lastlogintime == null) {
            if (other.lastlogintime != null) {
                return true;
            }
        } else if (!lastlogintime.equals(other.lastlogintime)) {
            return true;
        }
        return false;
    }

    /**
     * Verifies if the make of this DeviceAttributes object is different from the make of another DeviceAttributes
     * object.
     *
     * @param other The other DeviceAttributes object to compare with.
     * @return true if the make is different, false otherwise.
     */
    private boolean verifyMake(DeviceAttributes other) {
        if (make == null) {
            if (other.make != null) {
                return true;
            }
        } else if (!make.equals(other.make)) {
            return true;
        }
        return false;
    }

    /**
     * Verifies if the manufacturer of this DeviceAttributes object is different from the manufacturer of another
     * DeviceAttributes object.
     *
     * @param other The other DeviceAttributes object to compare with.
     * @return true if the manufacturer is different, false otherwise.
     */
    private boolean verifyManufacturer(DeviceAttributes other) {
        if (manufacturer == null) {
            if (other.manufacturer != null) {
                return true;
            }
        } else if (!manufacturer.equals(other.manufacturer)) {
            return true;
        }
        return false;
    }

    /**
     * Verifies if the model of this DeviceAttributes object is different from the model of another DeviceAttributes
     * object.
     *
     * @param other The other DeviceAttributes object to compare with.
     * @return true if the model is different, false otherwise.
     */
    private boolean verifyModel(DeviceAttributes other) {
        if (model == null) {
            if (other.model != null) {
                return true;
            }
        } else if (!model.equals(other.model)) {
            return true;
        }
        return false;
    }

    /**
     * Verifies if the series of this DeviceAttributes object is different from the series of another DeviceAttributes
     * object.
     *
     * @param other The other DeviceAttributes object to compare with.
     * @return true if the series is different, false otherwise.
     */
    private boolean verifySeries(DeviceAttributes other) {
        if (series == null) {
            if (other.series != null) {
                return true;
            }
        } else if (!series.equals(other.series)) {
            return true;
        }
        return false;
    }

    /**
     * Verifies if the vehicletype of this DeviceAttributes object is different from the vehicletype of another
     * DeviceAttributes object.
     *
     * @param other The other DeviceAttributes object to compare with.
     * @return true if the vehicletype is different, false otherwise.
     */
    private boolean verifyVehicletype(DeviceAttributes other) {
        if (vehicletype == null) {
            if (other.vehicletype != null) {
                return true;
            }
        } else if (!vehicletype.equals(other.vehicletype)) {
            return true;
        }
        return false;
    }

    /**
     * Verifies if the harmanId of this DeviceAttributes object is different from the harmanId of another
     * DeviceAttributes object.
     *
     * @param other The other DeviceAttributes object to compare with.
     * @return true if the harmanId is different, false otherwise.
     */
    private boolean verifyHarmanId(DeviceAttributes other) {
        if (harmanId == null) {
            if (other.harmanId != null) {
                return true;
            }
        } else if (!harmanId.equals(other.harmanId)) {
            return true;
        }
        return false;
    }

    /**
     * Returns a string representation of the DeviceAttributes object.
     * The string representation includes the values of the harmanId, country, manufacturer,
     * make, model, year, bodytype, series, vehicletype, and lastlogintime properties.
     *
     * @return A string representation of the DeviceAttributes object.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DeviceInfo [harmanId=");
        builder.append(harmanId);
        builder.append(", Country=");
        builder.append(country);
        builder.append(", Manufacturer=");
        builder.append(manufacturer);
        builder.append(", Make=");
        builder.append(make);
        builder.append(", Model=");
        builder.append(model);
        builder.append(", Year=");
        builder.append(year);
        builder.append(", Bodytype=");
        builder.append(bodytype);
        builder.append(", Series=");
        builder.append(series);
        builder.append(", Vehicletype=");
        builder.append(vehicletype);
        builder.append(", Lastlogintime=");
        builder.append(lastlogintime);
        builder.append("]");
        return builder.toString();
    }

}
