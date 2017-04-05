package com.moods_final.moods.recommend;

public class FoursquareVenue {
	private String name;
	private String city;
	private int flag;
	private String category;

	public FoursquareVenue() {
		this.name = "";
		this.city = "";
		this.flag=0;
		this.setCategory("");
	}


	public String getCity() {
		if (city.length() > 0) {
			return city;
		}
		return city;
	}

	public void setCity(String city) {
		if (city != null) {
			this.city = city.replaceAll("\\(", "").replaceAll("\\)", "");
			;
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public  int getFlag(){return  flag;}
	public  void setFlag(int a)
	{this.flag=a; }
	public String getName() {
		return name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}