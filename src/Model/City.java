package Model;

public class City {
    private int cityId;


    private String city;

    public City(int cityId, String city) {
        this.cityId = cityId;
        this.city = city;
    }


    public int getCityId() {
        return cityId;
    }

    public String getCity() {
        return city;
    }


    @Override
    public String toString() {
        return city;
    }
}
