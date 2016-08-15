package sprunth.dosfootball;

import com.google.gson.Gson;
import org.json.simple.JSONObject;
import sun.text.normalizer.Trie;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class TrieNode
{
    char nodeChar;              //character of the current node
    String nodeVal;             //store string or prefix string here
    String leafStr;             //if this is a leaf node, store the full name here
    List<TrieNode> children;    //child nodes, will be

    public TrieNode()
    {
        children = new ArrayList<TrieNode>();
        nodeChar = '\0';
        nodeVal = "";
    }

    public TrieNode(char nodeLabel, String prefix)
    {
        children = new ArrayList<TrieNode>();
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
            String normalized = normalizeChars(item);
            insert(normalized, fullName);
        }
    }

    public void insert(String toInsert, String fullName)
    {
        if(toInsert.equals(nodeVal))    //check if this is the node the string belongs to
        {
            leafStr = fullName;
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
        ArrayList<String> retList = new ArrayList<String>();   //return null if there are no similar string

        //find the node that matches the prefix (input)
        TrieNode prefixNode = search(input);

        if(prefixNode != null)
        {
            //find all the leaf nodes liked to that node
            prefixNode.getLeafStrings(retList);
        }

        return new Gson().toJson(retList);
    }

    private void getLeafStrings(ArrayList<String> fromPrefix)    //return strings with the prefix indicated by this node
    {
        if(fromPrefix.size() == 7)      //modification because we only need a few suggestions
        {
            return;
        }
        if(children.isEmpty())    //if we hit a leaf node
        {
            if(!fromPrefix.contains(leafStr))
            {
                fromPrefix.add(leafStr);
            }

            return;
        }
        else    //otherwise, traverse down a child node
        {
            for(ListIterator<TrieNode> it = children.listIterator(); it.hasNext(); )
            {
                TrieNode curChild = it.next();
                curChild.getLeafStrings(fromPrefix);
            }
        }
    }

    private TrieNode findChildWithChar(char target)
    {
        TrieNode curNode = null;
        for(ListIterator<TrieNode> it = children.listIterator(); it.hasNext(); )
        {
            curNode = it.next();
            if(curNode.nodeChar == target)
            {
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
