package Project2;

import java.util.*;
import java.io.*;
import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.alg.shortestpath.*;
import org.jgrapht.alg.interfaces.*;

public class wordladder {
    public static void main(String[] args) {
        
        file read  = new file();
        Scanner modeinput = new Scanner(System.in);
        word_graphs wordgraph = new word_graphs(read.returnMap());
        wordgraph.creatGraph();
        
        while(true){
        System.out.println("Enter Menu >>> (s/S = search , l/L = ladder)"
                        +"\n               (q/Q = quit , r/R = restart)");
        
            String mode = modeinput.nextLine().toLowerCase();
            
            
            switch(mode.toLowerCase()){
                case "s" : 
                            TreeMap<Character, TreeSet<String>> wordTS = read.returnTreemap();
                            int count=0;
                            System.out.println("word to search =");
                            Scanner insearch = new Scanner(System.in);
                            String word_search = insearch.nextLine();
                            char[] x = word_search.toCharArray();
                            TreeSet<String> allwo = wordTS.get(x[0]);
                            if(allwo != null){
                                System.out.println("========== Available words ==========");
                            for(String word : allwo){
                                if(word.toLowerCase().contains(word_search.trim().toLowerCase())){
                                    System.out.printf("%s  ",word);
                                    count++;
                                    if(count%10 == 0) System.out.println();
                                    }
                                }
                            }
                            else System.out.println("error input");
                            System.out.println();
                            break;
                case "l" : 
                System.out.println("----------word transformation----------");    
                Scanner w1 = new Scanner(System.in);
                System.out.println("enter word 1 ->");
                String wo1 = w1.nextLine().toLowerCase();
                System.out.print(" |\n to\n |\n");    
                Scanner w2 = new Scanner(System.in);
                System.out.println("enter word 2 ->");
                String wo2 = w2.nextLine().toLowerCase();
                
                    wordgraph.game(wo1,wo2);
                            break;
                case "q" : System.exit(0); break;
                case "r" : wordladder.main(args);
                default  : System.out.println("Incorrect input!!");;
            
              
        }
            
        }
        
    }
    
}

class word_graphs {
 private TreeMap<String , LinkedHashSet<String>> wordmap;
 private Graph<String, DefaultWeightedEdge> wordGraph;
 
 
 public word_graphs(TreeMap<String, LinkedHashSet<String>> map){
     wordmap = map;
     wordGraph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
     
 }
 public void creatGraph(){
     Graphs.addAllVertices(wordGraph, wordmap.keySet());
     Set<String> key = wordmap.keySet();
     double weight1 ;
     double weight2 ;
     double useweight=0;
     // 2 loop cuz for everyword check everyword
     for(String key1:key){
         for(String key2:key){
             if(key1.equalsIgnoreCase(key2))continue;
             
             LinkedHashSet<String> char_key1 = new LinkedHashSet(wordmap.get(key1));
             
             char_key1.removeAll(wordmap.get(key2));
             
             int c_samechar=0;
                 if(char_key1.size()==1){
                 LinkedHashSet<String> char_key2 = new LinkedHashSet(wordmap.get(key2));
                 List<String> list1 = new ArrayList<String>(wordmap.get(key1));
                 List<String> list2 = new ArrayList<String>(char_key2);   
                 
                    for(int i=0;i<list1.size();i++){
                    
                        String l1=list1.get(i);
                        String l2=list2.get(i);
                        if(l1.equals(l2)){
                     c_samechar++;
                        }
                    }
                
                if(c_samechar==4){
                char_key2.removeAll(list1);
                String x1 = char_key1.toString();
                String x2 = char_key2.toString();
                 
                 weight1 = checkDis(x1);
                 weight2 = checkDis(x2);
                 if(weight1>weight2) useweight=weight1-weight2;
                 else  useweight=weight2-weight1;
                 if(!wordGraph.containsEdge(key1,key2)){
                 wordGraph.addEdge(key1, key2 );
                 wordGraph.setEdgeWeight(key1,key2,useweight);
                        }
                    }
                }
                 if(char_key1.size()==0){
                    //System.out.printf("same 5\n");
                     useweight = 0;
                    if(!wordGraph.containsEdge(key1,key2)){
                wordGraph.addEdge(key1, key2 );
                wordGraph.setEdgeWeight(key1,key2,useweight);
                    }
                }
         }
         
     }
      //printGraph();
 }
 public void game(String word1,String word2) { /* Find Bacon degree of given actor */
     //check word have or not
        if(!wordmap.containsKey(word1.toLowerCase())) {
            System.out.printf("No word %s\n",word1);
            return ;
        }
    
    
       if(!wordmap.containsKey(word2.toLowerCase())) {
            System.out.printf("No word %s\n",word2);
            return ;
        }
    
        ShortestPathAlgorithm<String , DefaultWeightedEdge> dijshortrdt = new DijkstraShortestPath<String , DefaultWeightedEdge>(wordGraph);
        GraphPath<String ,DefaultWeightedEdge> graphpath = dijshortrdt.getPath(word1, word2);
        
        if(graphpath != null){
        List<DefaultWeightedEdge> transpath = graphpath.getEdgeList();//graphpath cant be null
            LinkedHashSet<String> unionset = new LinkedHashSet();
            LinkedHashSet<String> present = new LinkedHashSet();
            unionset.add(word1);
            
            double total=0;
            System.out.printf("start transformation \n%5s\n",word1);
            //loop edge
            for(DefaultWeightedEdge e: transpath){
                String s = wordGraph.getEdgeSource(e);
                String tg = wordGraph.getEdgeTarget(e);
                double weight = wordGraph.getEdgeWeight(e);
                //for print next word transform
                present.add(s);
                present.add(tg);
                present.removeAll(unionset);
                
                if(present.contains(s))System.out.printf("%5s",s);
                if(present.contains(tg))System.out.printf("%5s",tg);
                unionset.addAll(present);
                if(weight==0){System.out.printf("     elevator +%.1f\n",weight);}
                else {System.out.printf("     ladder   +%.1f\n",weight);}
                total += weight;
            }
            
            System.out.printf("total cost = +%.1f",total);
            System.out.println();
        }
        else System.out.printf("NO SOLUTION\n");
    //}
    
     
 }
 public double checkDis(String str) {
        
        str = str.toLowerCase();
       // char[] x = str.toCharArray();
       // char c = x[0];
        
        
      //// int index = c-'a';
       //System.out.printf("%s weight = %d \n", c,index);
        // Convert the string to lowercase to handle both uppercase and lowercase characters
        
        int index =0;
        // Iterate through each character in the string
        for (char c : str.toCharArray()) {
            
            if (c >= 'a' && c <= 'z') {
                index = c-'a';
                //System.out.printf("%s weight = %4d \n", c,index);
            }
        }

        // Calculate total weight
      /*  double totalWeight = 0;
        for (int i = 0; i < 26; i++) {
            
            
            totalWeight += (i + 1) * weights[i];
        }*/

        return index;
    }
 

 public void printGraph()
    {
	Set<DefaultWeightedEdge> allEdges = wordGraph.edgeSet();
	printDefaultWeightedEdges(allEdges, false);
    }

    public void printDefaultWeightedEdges(Collection<DefaultWeightedEdge> E, boolean f)
    {
	for (DefaultWeightedEdge e : E)
        {
            System.out.println(e);
	        
            // format our own output
            String source = wordGraph.getEdgeSource(e);
            String target = wordGraph.getEdgeTarget(e);
            double weight = wordGraph.getEdgeWeight(e);            
            
            if (f)  // print object content and weight
                System.out.printf("%20s - %20s   weight = %4.0f \n", 
                                  source, target, weight);

            else    // print only Country name and weight
		System.out.printf("%10s - %-10s   weight = %4.0f \n", 
                                  source, target, weight);                                  
        }
    }
}

class file {
    
    private String filename = "";
    private String path = "src/main/java/Project2/";
    private TreeMap<String, LinkedHashSet<String>> wordmap2 = new TreeMap();
   // private LinkedHashMap<String,Character> tmp;
    private TreeSet<String> treetemp;
    private LinkedHashSet<String> tmp2;
    private TreeMap<Character, TreeSet<String>> TM = new TreeMap();
    
    
    public file(){
        readfile();
    }
    
    public void readfile(){
        boolean openfile = false ;
        System.out.printf("Choose filename or type any filename :\n 1=words_5757.txt , 2=words_250.txt\n");
        System.out.printf("->");
        Scanner inputScanner = new Scanner(System.in);
        filename = inputScanner.nextLine();
        if(filename.equals("1")) filename ="words_5757.txt";
        if(filename.equals("2")) filename ="words_250.txt";
         
        while(!openfile){
            try(Scanner filereader = new Scanner(new File(path + filename))){
                openfile = true;
                System.out.printf("Please wait...\n");
                while(filereader.hasNext()){
                    String col = filereader.nextLine();
                    String words = col.trim();
                        char[] x = words.toCharArray();
                        //tmp = new LinkedHashMap();
                        treetemp = new TreeSet();
                        tmp2 = new LinkedHashSet();
                        treetemp.add(words);
                        //to same x[0](first char of word(key)) data can add to hashtemp
                       if(TM.containsKey(x[0])){
                            TreeSet setword = TM.get(x[0]);
                            setword.add(words);
                        }
                        else{
                            TM.put(x[0], treetemp);
                        }
                       
                        for(char ch : x){
                        String difchar =null;  
                        //condition to be differnt character
                            if(tmp2.contains(String.valueOf(ch)+"1")){
                                if(tmp2.contains(String.valueOf(ch)+"2")){
                                    difchar = String.valueOf(ch)+"3";
                                //tmp.put(difchar,ch);
                                tmp2.add(difchar);
                                }
                                else {
                                difchar = String.valueOf(ch)+"2";
                               // tmp.put(difchar,ch);
                                tmp2.add(difchar);
                                }
                            }
                            else {
                            difchar = String.valueOf(ch)+"1";  
                            //tmp.put(difchar,ch);
                            tmp2.add(difchar);
                            }
                        }
                            wordmap2.put(words, tmp2);
                            
                }
                

            }
            catch(Exception e){
                System.out.printf("File %s is not found. Please enter a new file name: ", filename);
                inputScanner = new Scanner(System.in);
                filename = inputScanner.nextLine();
            }
        }
        
    }
    
    public TreeMap<String, LinkedHashSet<String>> returnMap(){
        return wordmap2;
    }
    public TreeMap<Character, TreeSet<String>> returnTreemap(){
        return TM;
    }
    
}