package sprunth.dosfootball;

import sun.text.normalizer.Trie;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class TrieNode
{
    char nodeChar;              //character of the current node
    String nodeVal;             //store string or prefix string here
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
            insert(player);
        }
    }

    public void insert(String toInsert)
    {
        if(toInsert.equals(nodeVal) || nodeVal.length() == toInsert.length())      //return condition
        {
            return;
        }
        else if(toInsert.charAt(nodeVal.length()) == nodeChar)     //if we've traversed down the correct node
        {
            //check each of the child nodes
            for(TrieNode curNode : children)
            {
                if(curNode.nodeChar == toInsert.charAt(nodeVal.length()+1)) //if the node has the next char
                {
                    curNode.insert(toInsert);
                }
            }
        }
        else if(children.isEmpty() || nodeChar == '\0')     //if we hit the last node and the string is not finished or are at the root
        {
            //made a new node with 1 more char from the string
            TrieNode newNode = new TrieNode(toInsert.charAt(nodeVal.length()), toInsert.substring(0, nodeVal.length()+1));
            children.add(newNode);
            newNode.insert(toInsert);
        }

        //if we get here, we have a big problem
    }

    public TrieNode search(String target)
    {
        if(target.equals(nodeVal))      //return condition
        {
            return this;
        }
        else if(nodeChar == target.charAt(nodeVal.length()) || nodeChar == '\0')    //if you're continuing to traverse or at root
        {
            for(ListIterator<TrieNode> it = children.listIterator(); it.hasNext(); )    //checking every child
            {
                TrieNode curChild = it.next();
                if(target.charAt(nodeVal.length() + 1) == curChild.nodeChar)    //if the child node has the next letter in the string
                {
                    curChild.search(target);    //continue the search at the next level
                }
            }
        }

        //if mismatch or only partial string, not found and return null
        return null;
    }

    public ArrayList<String> topSuggestions(String input)
    {
        ArrayList<String> retList = new ArrayList<String>();   //return null if there are no similar string

        //find the node that matches the prefix (input)
        TrieNode prefixNode = search(input);

        if(prefixNode != null)
        {
            //find all the leaf nodes liked to that node
            prefixNode.getLeafStrings(retList);
        }


        return retList;
    }

    private void getLeafStrings(ArrayList<String> fromPrefix)    //return strings with the prefix indicated by this node
    {
        if(fromPrefix.size() == 7)      //modification because we only need a few suggestions
        {
            return;
        }
        if(children.isEmpty())    //if we hit a leaf node
        {
            fromPrefix.add(nodeVal);
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


}
