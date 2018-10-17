package sprunth.dosfootball;

import com.google.gson.Gson;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class TrieNode
{
    char nodeChar;              //character of the current node
    String nodeVal;             //store string or prefix string here
    List<String> names;         //list of names with this node's mapping
    List<TrieNode> children;    //child nodes, will be

    public TrieNode()
    {
        children = new ArrayList<>();
        names = new ArrayList<>();
        nodeChar = '\0';
        nodeVal = "";
    }

    public TrieNode(char nodeLabel, String prefix)
    {
        children = new ArrayList<>();
        names = new ArrayList<>();
        nodeChar = nodeLabel;
        nodeVal = prefix;
    }

    public void populateTree(ArrayList<String> playerList)
    {
        for(String player : playerList)
        {
            String[] stringParts = player.split(" ");
            for(String item : stringParts)
            {
                insert(item, player);
            }

        }
    }
    public void insert(String fullName)
    {
        String[] stringParts = fullName.split(" ");
        for(String item : stringParts)
        {
            String normalized = normalizeChars(item).toLowerCase();
            insert(normalized, fullName);
        }
    }

    public void insert(String toInsert, String fullName)
    {
        if(toInsert.equals(nodeVal))    //check if this is the node the string belongs to
        {
            if(!names.contains(fullName))
            {
                names.add(fullName);
            }
            return;
        }

        int nextCharIndex = nodeVal.length();
        TrieNode nextNode = findChildWithChar(toInsert.charAt(nextCharIndex));
        if(nextNode != null)
        {
            nextNode.insert(toInsert, fullName);
        }
        else
        {
            //create the next node and continue
            nextNode = new TrieNode(toInsert.charAt(nextCharIndex), toInsert.substring(0, nextCharIndex+1));
            children.add(nextNode);
            nextNode.insert(toInsert, fullName);
        }
    }

    public TrieNode search(String target)
    {
        if(target.equals(nodeVal))      //return condition
        {
            return this;
        }

        int nextCharIndex = nodeVal.length();
        TrieNode nextNode = findChildWithChar(target.charAt(nextCharIndex));
        if(nextNode != null)
        {
            return nextNode.search(target);
        }

        //if mismatch or only partial string, not found and return null
        return null;
    }

    public String topSuggestions(String input)
    {
        //find the node that matches the prefix (input)
        String searchStr = input.toLowerCase();

        //if there is a space, split and find the two parts
        String[] stringParts = searchStr.split(" ");

        if(stringParts.length > 1)
        {
            ArrayList<String> search1 = new ArrayList<>();    //first name search results
            ArrayList<String> search2 = new ArrayList<>();    //last name search results

            //TODO: sanity check these...or do something since pos [0] and [1] not necessarily a given
            //search the second word then match with the first word
            TrieNode firstNameNode = search(stringParts[0]);
            TrieNode lastNameNode = search(stringParts[1]);
            if(firstNameNode != null && lastNameNode != null)
            {
                //find all the leaf nodes liked to that node
                firstNameNode.getLeafStrings(search1);
                lastNameNode.getLeafStrings(search2);
            }

            ArrayList<String> retList = new ArrayList<>();
            for(String entry : search1)
            {
                if(search2.contains(entry))
                {
                    retList.add(entry);
                }
            }


            return new Gson().toJson(retList);
        }
        else
        {
            ArrayList<String> retList = new ArrayList<>();   //return null if there are no similar string
            TrieNode prefixNode = search(searchStr);

            if(prefixNode != null)
            {
                //find all the leaf nodes liked to that node
                prefixNode.getLeafStrings(retList);
            }

            return new Gson().toJson(retList);
        }
    }

    private void getLeafStrings(ArrayList<String> fromPrefix)    //return strings with the prefix indicated by this node
    {
        if(children.isEmpty())    //if we hit a leaf node
        {
            for(String entry : names)   //loop through the list of names at the leaf node
            {
                if(!fromPrefix.contains(entry))
                {
                    fromPrefix.add(entry);
                }
            }
        }
        else    //otherwise, traverse down a child node
        {
            for (TrieNode curChild : children) {
                curChild.getLeafStrings(fromPrefix);
            }
        }
    }

    private TrieNode findChildWithChar(char target)
    {
        TrieNode curNode;
        for (TrieNode aChildren : children) {
            curNode = aChildren;
            if (curNode.nodeChar == target) {
                return curNode;
            }
        }

        return  null;    //will return as null if nothing is found
    }

    //http://stackoverflow.com/questions/3322152/
    public String normalizeChars(String str)
    {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }
}
