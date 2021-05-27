package service;

import api.DataQueryService;
import api.Item;

import java.util.*;
import java.util.stream.Collectors;

public class DataQueryServiceImpl implements DataQueryService {
    Map<String, Item> items = new HashMap<String, Item>();
    public List<Item> query(String query){

        if (query.startsWith("EQUAL")){
            Equal();
        }
        if (query.startsWith("GREATER_THAN")) {
            GreaterThan();
        }
        if (query.startsWith("LESS_THAN")) {
            LessThan();
        }
        if (query.startsWith("AND")) {
            Intersection();
        }
        if (query.startsWith("OR")) {
            Union();
        }
        if (query.startsWith("NOT")) {
            Compliment();
        }
    }

    public List<Item> Equal(String property, String value) {
        List<Item> response = new ArrayList<Item>();
        if(property.startsWith("id")){
            String searchId = value.replaceAll("\"","");//Remove quote from string
            if (this.items.get(searchId) != null)//Check if the search id is not null
                response.add(this.items.get(searchId));
            else {
                System.out.print("No item has been found");
                return null;
            }
        }
        if(property.startsWith("title")){
            String searchTitle = value.replaceAll("\"","");//Remove quote from string
            if(items.containsValue(searchTitle)){
                for(Map.Entry<String, Item> entry : items.entrySet()){
                    if(entry.getValue().getTitle().equals(searchTitle)){
                        response.add(entry.getValue());
                    }
                }
            }
            else {
                System.out.print("No item has been found");
                return null;
            }
        }
        if(property.startsWith("content")){
            String searchContents = value.replaceAll("\"","");//Remove quote from string
            if(items.containsValue(searchContents)){
                for(Map.Entry<String, Item> entry : items.entrySet()){
                    if(entry.getValue().getContent().equals(searchContents)){
                        response.add(entry.getValue());
                    }
                }
            }
            else {
                System.out.print("No item has been found");
                return null;
            }
        }
        if(property.startsWith("views")){
            int searchViews = Integer.parseInt(value.replaceAll("\"",""));//Remove quote from string
            if(items.containsValue(searchViews)){
                for(Map.Entry<String, Item> entry : items.entrySet()){
                    if(entry.getValue().getViews() == searchViews){
                        response.add(entry.getValue());
                    }
                }
            }
            else {
                System.out.print("No item has been found");
                return null;
            }
        }
        if(property.startsWith("timestamp")){
            int searchTimestamps = Integer.parseInt(value.replaceAll("\"",""));//Remove quote from string
            if(items.containsValue(searchTimestamps)){
                for(Map.Entry<String, Item> entry : items.entrySet()){
                    if(entry.getValue().getTimestamp() == searchTimestamps){
                        response.add(entry.getValue());
                    }
                }
            }
            else {
                System.out.print("No item has been found");
                return null;
            }
        }
        return response;
    }
    public List<Item> GreaterThan(String property, String value){
        List<Item> response = new ArrayList<Item>();
        if(property.startsWith("views")){
            int searchViews = Integer.parseInt(value.replaceAll("\"",""));//Remove quote from string
            for(Map.Entry<String, Item> entry : items.entrySet()){
                if(entry.getValue().getViews() < searchViews){
                    response.add(entry.getValue());
                }
            }
        }
        else {
            System.out.print("Not a valid property");
            return null;
        }
        return response;
    }
    public List<Item> LessThan(String property, String value){
        List<Item> response = new ArrayList<Item>();
        if(property.startsWith("views")){
            int searchViews = Integer.parseInt(value.replaceAll("\"",""));//Remove quote from string
            for(Map.Entry<String, Item> entry : items.entrySet()){
                if(entry.getValue().getViews() < searchViews){
                    response.add(entry.getValue());
                }
            }
        }
        else {
            System.out.print("Not a valid property");
            return null;
        }
        return response;

    }
    public List<Item> Intersection(List<Item> a,List<Item> b){
        return a.stream().filter(b::contains).collect(Collectors.toList());
    }
    public List<Item> Union(List<Item> a,List<Item> b){
        Set<Item> s1 = new HashSet<>(a);
        s1.addAll(b);
        return new ArrayList<>(s1);
    }
    public List<Item> Compliment(List<Item> a){

    }

    public void save(Item item){
        items.put(item.getId(), item);
    }
}
