package service;

import api.DataQueryService;
import api.Item;

import java.util.*;
import java.util.stream.Collectors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import api.NoDataFound;
import api.QueryParseException;

public class DataQueryServiceImpl implements DataQueryService {
    Map<String, Item> items = new HashMap<String, Item>();
    public List<Item> query(String query) throws Exception{
        if(query.equals(""))
            return new ArrayList<Item>(items.values());
        Pattern pattern = Pattern.compile("^(EQUAL|GREATER_THAN|LESS_THAN)\\((.*?)\\,(.*)\\)");
        Matcher matcher = pattern.matcher(query);
        if(matcher.find()){
            String property = matcher.group(2);
            String value = matcher.group(3);

            if(matcher.group(1).equals("EQUAL"))
                return Equal(property, value);
            else if(matcher.group(1).equals("GREATER_THAN"))
                return GreaterThan(property, value);
            else if(matcher.group(1).equals("LESS_THAN"))
                return LessThan(property, value);
            else
                throw new QueryParseException("Invalid query", null);
        }
        Pattern aoPattern = Pattern.compile("^(AND|OR)\\((.*)\\)");
        Matcher aoMatcher = aoPattern.matcher(query);
        if(aoMatcher.find()){
            String functionArgs = aoMatcher.group(2);
            Pattern firstBracketP = Pattern.compile("^.*?\\(");
            Matcher firstBracketM = firstBracketP.matcher(functionArgs);
            //Divides the string into two arguments and sends them to and\or depending on their matcher.
            if(firstBracketM.find()){
                int pos = firstBracketM.end();
                int count = 1;
                while(pos < functionArgs.length() && count > 0){
                    if(functionArgs.charAt(pos) == '(')
                        count++;
                    else if(functionArgs.charAt(pos) == ')')
                        count--;
                    pos++;
                }
                if(count == 0) {
                    String arg1 = functionArgs.substring(0, pos);
                    String arg2 = functionArgs.substring(pos + 1, functionArgs.length());

                    if(aoMatcher.group(1).equals("AND")){
                        return Intersection(arg1,arg2);
                    }
                    else if(aoMatcher.group(1).equals("OR")){
                        return Union(arg1,arg2);
                    }
                    else
                        throw new QueryParseException("Invalid query", null);
                }
                else
                    throw new QueryParseException("Invalid query", null);
            }
            else
                throw new QueryParseException("Invalid query", null);
        }
        Pattern notPattern = Pattern.compile("^NOT\\((.*?)\\)");
        Matcher notMatcher = notPattern.matcher(query);
        if(notMatcher.find()){
            return Complement(notMatcher.group(1));
        }
        throw new QueryParseException("Invalid query", null);

    }

    public List<Item> Equal(String property, String value) throws Exception {
        List<Item> response = new ArrayList<Item>();
        if(property.equals("id")){
            String searchId = value.replaceAll("\"","");//Remove quote from string
            if (this.items.get(searchId) != null)//Check if the search id is not null
                response.add(this.items.get(searchId));
            else
                throw new NoDataFound(value);
        }
        if(property.equals("title")){
            String searchTitle = value.replaceAll("\"","");//Remove quote from string
            if(items.containsValue(searchTitle)){
                for(Map.Entry<String, Item> entry : items.entrySet()){
                    if(entry.getValue().getTitle().equals(searchTitle)){
                        response.add(entry.getValue());
                    }
                }
            }
            else
                throw new NoDataFound(value);
        }
        if(property.equals("content")){
            String searchContents = value.replaceAll("\"","");//Remove quote from string
            if(items.containsValue(searchContents)){
                for(Map.Entry<String, Item> entry : items.entrySet()){
                    if(entry.getValue().getContent().equals(searchContents)){
                        response.add(entry.getValue());
                    }
                }
            }
            else
                throw new NoDataFound(value);
        }
        if(property.equals("views")){
            int searchViews = Integer.parseInt(value);//Remove quote from string
            if(items.containsValue(searchViews)){
                for(Map.Entry<String, Item> entry : items.entrySet()){
                    if(entry.getValue().getViews() == searchViews){
                        response.add(entry.getValue());
                    }
                }
            }
            else
                throw new NoDataFound(value);
        }
        if(property.equals("timestamp")){
            int searchTimestamps = Integer.parseInt(value);//Remove quote from string
            if(items.containsValue(searchTimestamps)){
                for(Map.Entry<String, Item> entry : items.entrySet()){
                    if(entry.getValue().getTimestamp() == searchTimestamps){
                        response.add(entry.getValue());
                    }
                }
            }
            else
                throw new NoDataFound(value);
        }
        return response;
    }
    public List<Item> GreaterThan(String property, String value) throws Exception{
        List<Item> response = new ArrayList<Item>();
        if(property.equals("views")){
            int searchViews = Integer.parseInt(value.replaceAll("\"",""));//Remove quote from string
            for(Map.Entry<String, Item> entry : items.entrySet()){
                if(entry.getValue().getViews() > searchViews){
                    response.add(entry.getValue());
                }
            }
        }
        else if(property.equals("timestamp")){
            int searchTimeStamp = Integer.parseInt(value.replaceAll("\"",""));//Remove quote from string
            for(Map.Entry<String, Item> entry : items.entrySet()){
                if(entry.getValue().getTimestamp() > searchTimeStamp){
                    response.add(entry.getValue());
                }
            }
        }
        else
            throw new NoDataFound(value);
        return response;
    }
    public List<Item> LessThan(String property, String value) throws Exception{
        List<Item> response = new ArrayList<Item>();
        if(property.equals("views")){
            int searchViews = Integer.parseInt(value.replaceAll("\"",""));//Remove quote from string
            for(Map.Entry<String, Item> entry : items.entrySet()){
                if(entry.getValue().getViews() < searchViews){
                    response.add(entry.getValue());
                }
            }
        }
        else if(property.equals("timestamp")){
            int searchTimeStamp = Integer.parseInt(value.replaceAll("\"",""));//Remove quote from string
            for(Map.Entry<String, Item> entry : items.entrySet()){
                if(entry.getValue().getTimestamp() < searchTimeStamp){
                    response.add(entry.getValue());
                }
            }
        }
        else
            throw new NoDataFound(value);
        return response;

    }
    public List<Item> Intersection(String a,String b) throws Exception{

        try {
            List<Item> listA = query(a);
            List<Item> listB = query(b);
            return listA.stream().filter(listB::contains).collect(Collectors.toList());
        }
        catch(Exception e) {
            throw new QueryParseException("Invalid query", null);
        }


    }
    public List<Item> Union(String a,String b) throws Exception{
        try {
            List<Item> listA = query(a);
            List<Item> listB = query(b);
            Set<Item> s1 = new HashSet<>(listA);
            s1.addAll(listB);
            return new ArrayList<>(s1);
        }
        catch(Exception e) {
            throw new QueryParseException("Invalid query", null);
        }
    }
    public List<Item> Complement(String a)throws Exception{
        try {
            List<Item> listA = query(a);
            List<Item> itemValue = new ArrayList<Item>(items.values());
            itemValue.removeAll(listA);
            return itemValue;
        }
        catch(Exception e) {
            throw new QueryParseException("Invalid query", null);
        }
    }

    public void save(Item item) throws Exception{
        items.put(item.getId(), item);
    }
}
