import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

//ISLA Chatbot

/* Author: Darwin Chen
 * 
 * Isla is a chatbot written with the purpose to have conversational chats
 * with users. It have services that would require internet connection to 
 * provide up to date and current info adding to its functionalities. 
 * 
 */

public class Isla extends Application {
    
	static Thread loadingThread = new Thread();
	static Thread mainThread = new Thread();
	
	private static int EmoLib = 0;
	private static int IntLib = 0;
	private static int RepLib = 0;
	
	private static double maxMatch = 0;
	
	static TextArea output = new TextArea();
	static BorderPane root = new BorderPane();
	static Boolean isLoaded = false;
	
	//System check
    static HashMap<Boolean, String> AllOn = new HashMap<Boolean, String>();
	//Services to check
	static Boolean NLP = false;
	static Boolean Time = false;
	static Boolean Weather = false;
	static Boolean evaemo = false;
	static Boolean ezres = false;
	static Boolean Loger = false;
	static Boolean Dictionary = false;
	static Boolean diction = false;
	static Boolean EzAnswer = false;
	static Boolean isMulti = false;
	static HashMap<String, List<String>> EzRes = new HashMap<String, List<String>>();
	static HashMap<String, List<String>> EvaRes = new HashMap<String, List<String>>();
	static HashMap<String, String> EzResWeather = new HashMap<String, String>();
	static String UserQ = "";
	static int UserQParts = 0;
	static int i = 1;
	static String IslaRes = "";
	static String MatchIntentRes = "";
	static String Fword = "";
	static String Obj = "";
	static String DictionWord = "";
	static String DictionRes = "";
	static int SentEv = 2;
	/*sentiment analysis by StanfordNLP has the output of 0 - 4
	where the 0 being most negative up to 4 being most positive */
	static double OvEmoEval = 0;
	static String WeatherAn = "";
	static String weatherTag = "";
	static String TimeAn = "";
	static String DateAn = "";
	static String currentTime = "";
	static String currentDate = "";
	static String Intentf = "";
	static String SystemStat = "";
	static String finalDef = "";
	static int intentF = 0;
	//if intentF is equal to 1, then user input is a question
	//if intentF is equal to 0, then user input is a statment

	static List<String> wordpos = new LinkedList<String>();
	static List<String> Userq = new LinkedList<String>();
	static List<String> allNN = new LinkedList<String>();
	static List<String> NameEn = new LinkedList<String>();
	static List<String> QuestionW = new ArrayList<String>();
	static List<String> QuestionNN = new ArrayList<String>();
	
	//Weather
	static HashMap<String, Boolean> WeatherNow = new HashMap<String, Boolean>(); 
	static List<String> weatherPro = new ArrayList<String>();
	static List<Integer> weatherData = new ArrayList<Integer>();
	
	//Memory manager
	static HashMap<String, String> Bios = new HashMap<String, String>();
	static HashMap<String, List<String>> ULikes = new HashMap<String, List<String>>();
	static List<String> PerName = new ArrayList<String>();
	static HashMap<String, List<String>> UDlikes = new HashMap<String, List<String>>();
	static HashMap<String, List<String>> Friends = new HashMap<String, List<String>>();
	static HashMap<String, String> Family = new HashMap<String, String>(); 
    
	//EmoAna is used to store all the emotion that were found in the input
	static List<String> EmoAna = new ArrayList<String>();
	
	//logging
	private static String LogPath = "C:\\Users\\darwi\\eclipse-workspace\\ChatBot\\zLog\\";	
	
	static String[] userq = {};
	//Array Library for Services
	private static String[] CurrentSer = {"Weather", "Time", "NLP", "Sentiment", "Dictionary"};
	//Array Library for emotional anaylsis

	//Array Library for question POS
	private static String[] questionw = {"WH", "WDT", "WRB", "WP"};
	//Array Library for question NN
	private static String[] questionNN = {"VBP", "VBZ"};
	//Initializing the Emotion library
	static HashMap<String, String> EvaEmo = new HashMap<String, String>();
	static HashMap<String, String> EvaAna = new HashMap<String, String>();
	
	@FXML
    private PasswordField enterPress;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
			
			// Create a Runnable
	        Runnable task = new Runnable()
	        {
	            public void run()
	            {
	                try {
						MemLoader();
					} catch (InterruptedException | IOException | ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
	        };
	 
	        // Run the task in a background thread
	        loadingThread = new Thread(task);
	        // Terminate the running thread if the application exits
	        loadingThread.setDaemon(true);
	        loadingThread.start();

			Parent pane = FXMLLoader.load(Isla.class.getResource(("Loader.fxml")));				
			Scene loading = new Scene(pane, 250, 100);
			primaryStage.setTitle("Loading ISLA...");
			primaryStage.setResizable(false);
			primaryStage.setScene(loading);
			primaryStage.show();
			
			//wait 5 seconds for all NLP to initialize and load in data from library and services
			Thread.sleep(7000);

			TextField input = new TextField();
			input.setAlignment(Pos.CENTER_LEFT);		
					
			output.prefHeight(600);
			output.setWrapText(true);
			output.setEditable(false);
							
			MenuBar mb = new MenuBar();
			Menu m1 = new Menu("Libraries");
			Menu m2 = new Menu("Current Services");
			Menu Cur = new Menu("Currently");
			//Array Library for Libraries
			String[] CurrentLib = {"EVAEmo: " + EmoLib,
									"EVAInt: " + IntLib, 
									"EVARes: " + RepLib,
									};							

			//Adding library to the library list in the menu bar
			for(int i=0; i<CurrentLib.length; i++) {
				MenuItem files = new MenuItem(CurrentLib[i]);
				m1.getItems().add(files);
			}
			for(int i=0; i<CurrentSer.length; i++) {
				MenuItem service = new MenuItem(CurrentSer[i]);
				m2.getItems().add(service);
			}
							
			Menu allon = new Menu("SystemStat");
			MenuItem sysstat = new MenuItem(SystemStat);
			
			//ISLA Version Systems
			//B- for beta
			//A- for alpha/final
			MenuItem sysVersion = new MenuItem("Version: B-v0.2");
			
			allon.getItems().add(sysstat);
			allon.getItems().add(sysVersion);

			MenuItem date = new MenuItem(currentDate);
			MenuItem wea = new MenuItem(weatherTag + ", " + weatherData.get(0) + "°C");
			Cur.getItems().add(date);
			Cur.getItems().add(wea);
							
			//Adding library to the library list in the menu bar
			mb.getMenus().addAll(m1, m2, Cur, allon);
				
			root.setTop(mb);
			root.setCenter(output);
			BorderPane.setMargin(output, new Insets(10));
			root.setBottom(input);
			BorderPane.setMargin(input, new Insets(10));
				
			Scene scene = new Scene(root, 400, 300);
			primaryStage.setTitle("Isla");
			primaryStage.setResizable(false);
			primaryStage.setScene(scene);
			primaryStage.show();
						
			//Initial greetings with some info
			String hour = currentTime.split(":")[0];
			int Hour = Integer.parseInt(hour);
			String timedy = "";

			if(6 <= Hour && Hour <= 12) {
				timedy = "Good morning";
			} else if(12 <= Hour && Hour <= 18) {
				timedy = "Good afternoon";
			} else if(18 <= Hour && Hour <= 24) {
				timedy = "Good evening";
			} else {
				timedy = "Hey there";
			}
				
			Random rand = new Random();
			String User = PerName.get(rand.nextInt(PerName.size()));
			
			output.setText("-->Isla: " + timedy + ", " + User + 
					". Isla here. （‐＾▽＾‐）ﾉ \n");
				
			input.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				switch(event.getCode()) {
				case ENTER:
					//Clearing out the user input and ready up for the next input
					wordpos.clear();
					Userq.clear();
					NameEn.clear();
					EmoAna.clear();	
					allNN.clear();
					UserQ = "";
					Obj = "";
					DictionWord = "";
					isMulti = false;
					i = 1;
					maxMatch = 0;
					
					UserQ = input.getText();
					input.setText("");
					time();
					Nlp();
					/*Putting the input to Dialog area
					and adding a new line*/
					output.appendText("-->You: " + UserQ + "\n");
							
					try {
						MainPro();
					} catch (ParseException | IOException e2) {
						e2.printStackTrace();
					}
							
					try {
						Logger();
					} catch (IOException e1) {
						System.out.println("Error occured when trying to create log! (╯•﹏•╰)");
					}
							
					//Eva responds
					try {
						Response();
					} catch (IOException e1) {
						System.out.println("Error occured when trying to generate response! (╯•﹏•╰)");
					}
							
					//using stacks for storing preivous conversations for thread tracking
					
					//

					break;
				default:
					break;
				}
			}
					
		});
			
	}

		
	public static void main(String[] args) throws InterruptedException{	

		launch(args);
  
	}   
		
		public static void MemLoader() throws InterruptedException, 
		IOException, ParseException{

			//Memory load and write functions
			databaseLoad();	
			//MemWrite();

			//Services
			time();
			weather();	

			Loger = true;	
			Dictionary = true;
			NLP = true;

			//System Check
			AllOn.put(NLP, "NLP");
			AllOn.put(Time, "Time");
			AllOn.put(Weather, "Weather");
			AllOn.put(ezres, "EzRes");
			AllOn.put(evaemo, "EVAEmo");
			AllOn.put(Loger, "Logger");
			AllOn.put(Dictionary, "Dictionary");
			
			if(AllOn.containsKey(false)) {
				SystemStat = AllOn.get(false) + " is not online";
			} else {
				SystemStat = "All services are now online";
			}

			Nlp();

			String LogName = "IslaLog" + currentDate;  
			File Log = new File(LogPath + LogName + ".txt");		
		
			if(!Log.exists()) {
		      Log.createNewFile();
		    }
			
			FileWriter log = new FileWriter(Log, true);
			BufferedWriter logging = new BufferedWriter(log);
			logging.write(currentTime + "\n" + 
						"System: System Starting" + "\n");
			
			isLoaded = true;
		
			logging.write("System: " + SystemStat + "\n");
			logging.close();
			
		}
  
	    public static void Response() throws IOException {
	    	LoggerR();
	    	
   			output.appendText("-->Isla: " + IslaRes + "\n");
   			
   			IslaRes = "";
   			//timer function below, will output if user do not respond in sometime to check for attention
   			
		}
	    
		public static void MainPro() throws ParseException, IOException {		
			QuestionW = Arrays.asList(questionw);
			QuestionNN = Arrays.asList(questionNN);
			
			EmotionP();
			IntentP();	
		}

		public static void IntentP() throws ParseException, IOException {
			//IntentP processes the sentence first and prep it for IntentDeep
			String WorPo = "";
			WorPo = String.join(" ", wordpos);
			
			//finding comma or period to interpret sentences if needed
			if(UserQ.contains(",") || UserQ.contains(".")) {

				for(int i=0; i<UserQ.split("[,.]").length; i++) {
					
					List<String> Poser = new LinkedList<String>
					(Arrays.asList(WorPo.split("[,.]")[i].trim()));
					List<String> Sentence = new LinkedList<String>
					(Arrays.asList(UserQ.split("[,.]")[i].trim()));
					Fword = Poser.get(0).split(" ")[0];
					//Bait is created to map and allocate value in LinkedLists for better performance
					List<String> Baitwordpos = new LinkedList<String>(Arrays.asList(Poser.get(0).split(" ")));
					wordpos = Baitwordpos;
					List<String> BaitUserq = new LinkedList<String>(Arrays.asList(Sentence.get(0).split(" ")));
					Userq = BaitUserq;
					IntentDeep();
				}		
			} else {
				Fword = wordpos.get(0);
				IntentDeep();
			}		
		}
		
		public static void IntentDeep() throws ParseException, IOException {
			int Question = 0;
			int Statement= 0;
			
			if(Userq.contains("?")||QuestionW.contains(Fword)) {
				Question++;
				Intentf = "a question";
			} else if(Userq.contains(".")||Userq.contains("!")){
				Statement++;
				Intentf = "a statement";
			} else {
				Statement++;
				Intentf = "most likely a statement";
			}		
	    	
	    	if(Question > Statement && wordpos.size() > 2) {
	    		intentF = 1;
				
				int indexNN = 0;
				List<Integer> NNAll = new LinkedList<Integer>();
				List<String> allNN = new LinkedList<String>();
				Obj = ""; //The object/info the user is asking for in an question
				String NN = "";
				NN = wordpos.get(1); //Seeing if the second word is "is" or "are"
				DictionWord = Userq.get(Userq.size() - 1);
				
				if(QuestionNN.contains(NN)) {	
					if(wordpos.contains("CC")) {				
						for(int p=0;p<wordpos.size();p++) {		
							if(wordpos.get(p).equals("NN")||wordpos.get(p).equals("NNP")||
							   wordpos.get(p).equals("NNS")||wordpos.get(p).equals("JJ")) {
								NNAll.add(p);
							}
						}
						
						EzAnswer = true;	
						
						for(int o=0;o<NNAll.size();o++) {
						 allNN.add(Userq.get(NNAll.get(o)).toLowerCase().replaceAll("[^a-zA-Z0-9]", " "));
						}
						
						System.out.println(allNN);
						//multiple functions might need to be called
					} else if(wordpos.contains("NN")){			
						indexNN = wordpos.indexOf("NN");
						Obj = Userq.get(indexNN).toLowerCase().replaceAll("[^a-zA-Z0-9]", "");;
						EzAnswer = true;
						
						System.out.println(Obj);
						ResPro();
					} else if(wordpos.contains("NNS")) {
						indexNN = wordpos.indexOf("NNS");
						Obj = Userq.get(indexNN).toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
						EzAnswer = true;
						
						System.out.println(Obj);
						ResPro();
					} else if(wordpos.contains("NNP")) {
						indexNN = wordpos.indexOf("NNP");
						Obj = Userq.get(indexNN).toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
						EzAnswer = true;
						
						System.out.println(Obj);
						ResPro();
					} else if(wordpos.contains("JJ")) {
						indexNN = wordpos.indexOf("JJ");
						Obj = Userq.get(indexNN).toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
						EzAnswer = true;
						
						System.out.println(Obj);
						ResPro();
					} else {
						MatchPro();
					}
				} else {
					MatchPro();
				}
	    } else if(Statement > Question){
	    	intentF = 0;

			MatchPro();
	    }
	}
		
		public static void MatchPro() throws ParseException, IOException {

			if(Userq.contains(",")||Userq.contains("?")||Userq.contains(".")) {
				Userq.remove((Userq.size() - 1));
			}
			
			Collection<String> inputClean = Userq;

			//code to scan EvaRes for patterns that match 
		    EvaRes.forEach((k,v) -> {
		    	for (int i = 0; i < v.size(); i++) {
		    		double s, p;
		    		
		    		Collection<String> inWords = new ArrayList<String>(Arrays.asList(v.get(i).split(" ")));
		    		Collection<String> similar = new ArrayList<String>(inWords);
		    		
		    		similar.retainAll(inputClean);
		    		s = similar.size();
		    		p = inputClean.size();
		    		
		    		if(maxMatch < s/p) {
		    			maxMatch = (s/p);
		    			Obj = k;
		    		} else {
		    			
		    		}
		    		
		    		System.out.println(s + " " + p);					    		
		    		System.out.println(maxMatch);
		    	}
		    }
	        );
		    ResPro();
		}
		
		public static void ResPro() throws ParseException, IOException {
			/*ResPro is the main response generation script
			 * main functions of response generation. This method
			 * will include both the EzRez function, command processing
			 * and also the statment processing functions
			 */
			
			MatchIntentRes = "";
			
			if(allNN.size() > 0) {
				Obj = allNN.get(i - 1);
			}
			
			//checking to see if dictionary needs to be called
			diction = false; //resetting diction
			if(Obj.equals("meaning")||Obj.equals("definition")) {
				diction = true;
				DictionWord = Userq.get(Userq.size() - 1);
			} else if(Obj.equals("mean")) {
				diction = true;
				DictionWord = Userq.get((Userq.indexOf("mean") - 1));
			}
			
			Random rand = new Random();
			
			switch(Obj) {
			case "weather":
				weather();
				MatchIntentRes = WeatherAn;
				break;
			case "time":
				time();
				MatchIntentRes = TimeAn;
				break;
			case "date":
				time();
				MatchIntentRes = DateAn;
				break;
			case "greet":
				MatchIntentRes = EzRes.get("greet").get(rand.nextInt(EzRes.get("greet").size()));
				break;
			case "greetQA":
				MatchIntentRes = EzRes.get("greetQA").get(rand.nextInt(EzRes.get("greetQA").size()));
				break;
			default:
				if(diction == true) {
					Dictionary();
					MatchIntentRes = DictionRes;
				} else {
					//Using ELIZA response generating method
					
					//
				}	 
			}
			
			if(isMulti == true) {

				if(i < UserQParts || i < allNN.size()) {
					                                                                                                            
					IslaRes = IslaRes + MatchIntentRes + "\n";

					i++;

				} else {
					
					IslaRes = IslaRes + MatchIntentRes + "\n";
					
			    	LoggerR();
					Logger();

				}
				
			} else {
				IslaRes = MatchIntentRes;

		    	LoggerR();
				Logger();

			}

		}
		
		public static void EmotionP() {
			/*The emotion process so far just scans through the 
			 * EVAEmo library for emotions found in the input and
			 * at the same time working together with the StandfordNLP's
			 * sentiment analysis to let Eva have an general idea of how 
			 * the user is feeling
			 */
			int Ana = 0;
			List<String> ana = new LinkedList<String>();
			HashMap<String, Integer> Anaa = new HashMap<String, Integer>();
			Anaa.put("hate", 0);
			Anaa.put("sad", 1);
			Anaa.put("fear", 2);
			Anaa.put("neutral", 3);
			Anaa.put("desire", 4);
			Anaa.put("happy", 5);
			//Clearing up the input for analysis
			UserQ = UserQ.replaceAll("\\W", " ").toLowerCase();
			userq = UserQ.split(" ");
			
			for(int i=0; i<userq.length; i++) {
				Boolean EmoSense = EvaEmo.containsKey(userq[i]);			
					if(EmoSense) {
						EmoAna.add(EvaEmo.get(userq[i]));
					} 
			}
			//Emoana (list) is used to keep all the emotions once 
			//EmoAna is then used to see how often a emotion was found
			List<String> Emoana = new LinkedList<String>();
			Emoana = EmoAna.stream().distinct().collect(Collectors.toList()); 
			
			for(int i=0; i<Emoana.size(); i++) {	
				if(EvaAna.containsKey(Emoana.get(i))) {
					ana.add(EvaAna.get(Emoana.get(i)));
				}
				//Test code
				//System.out.println(Emoana.get(i) + " has appeared " + 
				//Collections.frequency(EmoAna, Emoana.get(i)) + " times in the user input");
			}
		
			for(int i=0; i<ana.size(); i++) {
				Ana = Ana + Anaa.get(ana.get(i));
			}
			if(ana.size() == 0) {
				OvEmoEval = 5 * 10 + SentEv * 12.5;
			} else if(ana.size() > 0) {
				OvEmoEval = Ana * 10 + SentEv * 12.5;
			}			
		}

		public static void Nlp() {
			
			org.apache.log4j.BasicConfigurator.configure();
	    	
	        Properties props = new Properties();
	        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, sentiment");
	        props.setProperty("ner.applyFineGrained", "false");
	        
	        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	        // read some text in the text variable
	        String text = UserQ;
	        // create an empty Annotation just with the given text
	        Annotation document = new Annotation(text);

	        // run all Annotators on this text
	        pipeline.annotate(document);
	        
	        List<CoreMap> sentences = document.get(SentencesAnnotation.class);

	        for(CoreMap sentence: sentences) {
	          // traversing the words in the current sentence
	          // a CoreLabel is a CoreMap with additional token-specific methods
	        for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
	            // this is the text of the token
	            String word = token.get(TextAnnotation.class);
	            // this is the POS tag of the token
	            String pos = token.get(PartOfSpeechAnnotation.class);
	            // this is the NER label of the token
	            String ner = token.get(NamedEntityTagAnnotation.class);
	            // this sets the tree and output of the Sentiment analysis to int SentEv
	            Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);  
	            SentEv = RNNCoreAnnotations.getPredictedClass(tree);
	            
	            word = word.toLowerCase();     
	            wordpos.add(pos);
	            System.out.println(wordpos);
	            Userq.add(word);
	            System.out.println(Userq);
	            NameEn.add(ner);
	        } 
	    }
    }
		//Main Memory Writer
				@SuppressWarnings("unchecked")
				public static void MemWrite() {

					List<String> mate = new ArrayList<String>();
					List<String> mateIn = new ArrayList<String>();			
					Friends.forEach((key, val) -> {
						mate.add(key);
						String anw = "";
						for(int i=0; i<val.size(); i++) {
							anw = anw + val.get(i) + "_";
						}
						mateIn.add(anw);
					});
					
					JSONObject Friends = new JSONObject();
			       	for(int y=0; y<mate.size(); y++) {
			       		Friends.put(mate.get(y), mateIn.get(y));
			       	}
			       	
			       	JSONObject Super =  new JSONObject();
			       	JSONObject Familya = new JSONObject();
			       	JSONObject UserBio = new JSONObject();
			       	Family.forEach((key, val) -> {
			       		Familya.put(key, val);
			       	});  	
					JSONObject Likes = new JSONObject();
					JSONObject Dislikes = new JSONObject();
					JSONObject UserBios = new JSONObject();
					JSONObject Conn = new JSONObject();
					UserBios.put("Name", Bios.get("Name"));
					UserBios.put("Birthday", Bios.get("Birthday"));
					UserBios.put("Gender", Bios.get("Gender"));
					UserBios.put("Prefered Name", PerName);
					Likes.put("Food", ULikes.get("Food"));
					Likes.put("Game", ULikes.get("Game"));
					Likes.put("Watch", ULikes.get("Watch"));
					Likes.put("Others", ULikes.get("Others"));
					Dislikes.put("Food", UDlikes.get("Food"));
					Dislikes.put("Others", UDlikes.get("Others"));			
					Conn.put("Family", Familya);
					Conn.put("Friends", Friends); 
					
					UserBios.put("Like", Likes);
					UserBios.put("Dislike", Dislikes);
					UserBio.put("UserBios", UserBios);
					Super.put("Connections", Conn);
					Super.put("Creator", UserBio);
					
					try (FileWriter file = new FileWriter("IslaMem.txt", false)) {
			            file.write(Super.toString());
			            file.close();
			        } catch(Exception e){
			            System.out.println(e);
			        }
					
					/* Writing EzRes
					List<String> time = new ArrayList<String>();
					time.add("The current time is %time and the date is %date, a %week");
					time.add("It is currently %time right now");
					time.add("The time is %time");
					List<String> date = new ArrayList<String>();			
					date.add("Check the menu bar! Baka");
					date.add("Today is %date, and it's a %week");
					date.add("It is %date, %week");
					List<String> greet = new ArrayList<String>();
					greet.add("Hey there, %user");
					greet.add("Hello, %user");
					greet.add("Good to see you %user");
					List<String> greetQA = new ArrayList<String>();
					greetQA.add("All good, %user");
					greetQA.add("Can't be better, just cleaning up some logs");
					greetQA.add("You know... A bit more attention from you would be good...");
					greetQA.add("Always ready to go back to sleep.");
					greetQA.add("Could always be better with more RAM %user *Wink*");
					List<String> dic = new ArrayList<String>();
					dic.add("The meaning for the word %word is: \n %definition");
					dic.add("The word %word means: \n %definition");
					dic.add("%word means: \n %definition");
					dic.add("The definition for %word is: \n %definition");
					JSONObject EzResponse = new JSONObject();
					JSONObject timeA = new JSONObject();
					JSONObject greetA = new JSONObject();
					JSONObject weatherA = new JSONObject();
					JSONObject dicA = new JSONObject();
					
					timeA.put("time", time);
					timeA.put("date", date);
					
					greetA.put("greet", greet);
					greetA.put("greetQA", greetQA);

					weatherA.put("an", "The current temperature is %temp with wind speeds of %wind \n It is currently %type");
					weatherA.put("Windy", "A bit windy today, make sure to wear a jacket");
					weatherA.put("Cloudy", "Not that much sun with lots of clouds, good day");
					weatherA.put("Rainy", "Bring a umbrella or wear a jacket and enjoy the rain today");
					weatherA.put("Snowy", "Watch out for snow");
					weatherA.put("Cold", "Make sure you wear lots and stay warm, quite cold today");
					weatherA.put("Hot", "Hot day today. Hate it");
					weatherA.put("Comfortable", "Very comfortable weather today overall");
					
					EzResponse.put("time", timeA);
					EzResponse.put("greet", greetA);
					EzResponse.put("dictionary", dic);
					EzResponse.put("weather", weatherA);
					
					try (FileWriter file = new FileWriter("EzRes.txt", false)) {
			            file.write(EzResponse.toString());
			            file.close();
			        } catch(Exception e){
			            System.out.println(e);
				} */
				}
				public static void databaseLoad() throws ParseException {
					//Initialize the database 
					//EvaEmo is all the words with a emotional value
					try {	
						Scanner Evaemo = new Scanner(new FileReader("EvaEmo.txt"));
							while(Evaemo.hasNextLine()){
								//Getting the data ready to use
								//Putting all the Emotion and linked words into EvaEmo hashmap
								String Original = Evaemo.nextLine();
								Original = Original.replaceAll("[,' ]", "").replaceAll(":", " ");
								String[] TempWr = Original.split("\\s");
								EvaEmo.put(TempWr[0],TempWr[1]);
								EmoLib++;	
								evaemo = true;
							}
					} catch (FileNotFoundException e1) {
						System.out.println("Error loading database EvaEmo");
					}	
					
					//EzRes
					try {	
						String resultE = "";
						Scanner EzResl = new Scanner(new FileReader("EzRes.txt"));
						while(EzResl.hasNext()){
							resultE = resultE + " " + EzResl.next();
						}
						
						JSONParser jsonParser = new JSONParser();
						Object obj = jsonParser.parse(resultE);
							JSONObject EzResponse = (JSONObject) obj;
							JSONObject timeA = (JSONObject) EzResponse.get("time");
							JSONObject greetA = (JSONObject) EzResponse.get("greet");
							JSONObject weatherA = (JSONObject) EzResponse.get("weather");
							
							JSONArray time = (JSONArray) timeA.get("time");
							JSONArray date = (JSONArray) timeA.get("date");
							EzRes.put("time", Arrays.asList(Arrays.copyOf(time.toArray(), time.size(), 
									String[].class)));
							EzRes.put("date", Arrays.asList(Arrays.copyOf(date.toArray(), date.size(), 
									String[].class)));
							JSONArray greet = (JSONArray) greetA.get("greet");
							JSONArray greetQA = (JSONArray) greetA.get("greetQA");
							EzRes.put("greet", Arrays.asList(Arrays.copyOf(greet.toArray(), greet.size(), 
									String[].class)));
							EzRes.put("greetQA", Arrays.asList(Arrays.copyOf(greetQA.toArray(), 
									greetQA.size(), String[].class)));
							JSONArray dictionary = (JSONArray) EzResponse.get("dictionary");
							EzRes.put("dictionary", Arrays.asList(Arrays.copyOf(dictionary.toArray(), 
									dictionary.size(), String[].class)));
							
							EzResWeather.put("Answer", weatherA.get("an").toString());
							EzResWeather.put("Snowy", weatherA.get("Snowy").toString());
							EzResWeather.put("Rainy", weatherA.get("Rainy").toString());
							EzResWeather.put("Cloudy", weatherA.get("Cloudy").toString());
							EzResWeather.put("Windy", weatherA.get("Windy").toString());
							EzResWeather.put("Cold", weatherA.get("Cold").toString());
							EzResWeather.put("Hot", weatherA.get("Hot").toString());
							EzResWeather.put("Comfortable", weatherA.get("Comfortable").toString());
							
							ezres = true;
							EzResl.close();
					} catch (FileNotFoundException e1) {
						System.out.println("Error loading database EzRes");
					}
					
					//EvaRes contains all the example statments and phrases which are
					//used to identify commands and statments
					try {	
						String resultE = "";
						Scanner EvaRes1 = new Scanner(new FileReader("EvaRes.txt"));
						while(EvaRes1.hasNext()){
							resultE = resultE + " " + EvaRes1.next();
						}
						
						JSONParser jsonParser = new JSONParser();
						Object obj = jsonParser.parse(resultE);
							JSONObject EvaResponse = (JSONObject) obj;
							JSONObject greetQ = (JSONObject) EvaResponse.get("greet");
							
							JSONArray greet = (JSONArray) greetQ.get("greet");
							JSONArray greetQA = (JSONArray) greetQ.get("greetQA");
							EvaRes.put("greet", Arrays.asList(Arrays.copyOf(greet.toArray(), greet.size(), 
									String[].class)));
							EvaRes.put("greetQA", Arrays.asList(Arrays.copyOf(greetQA.toArray(), 
									greetQA.size(), String[].class)));
							
							JSONArray time = (JSONArray) EvaResponse.get("time");
							EvaRes.put("time", Arrays.asList(Arrays.copyOf(time.toArray(), time.size(), 
									String[].class)));
							
							JSONArray weather = (JSONArray) EvaResponse.get("weather");
							EvaRes.put("weather", Arrays.asList(Arrays.copyOf(weather.toArray(), 
									weather.size(), String[].class)));
							
							EvaRes.forEach((k,v) -> {
						    	for (int i = 0; i < v.size(); i++) {
									RepLib++;
						    	}
						    }	
					        );

							EvaRes1.close();
					} catch (FileNotFoundException e1) {
						System.out.println("Error loading database EvaRes");
					}
					
					//EmoAna is categorizing all the values from EvaEmo into 
					//even more generalized categories
					try {	
						Scanner Emoana = new Scanner(new FileReader("EmoAna.txt"));
							while(Emoana.hasNextLine()){
								//Getting the data ready to use
								//Putting all the Emotion and linked words into EvaEmo hashmap
								String Original = Emoana.nextLine().replaceAll(" ", "");
								String[] TempWr = Original.split(",");
								EvaAna.put(TempWr[0],TempWr[1]);
								
							}
					} catch (FileNotFoundException e1) {
						System.out.println("Error loading database EvaAna");
						
					}
					
					//Main Memory 
					try {	
						Scanner Evamem = new Scanner(new FileReader("IslaMem.txt"));
						String result = Evamem.next();
						while(Evamem.hasNext()){
							result = result + " " + Evamem.next();
						}

			            	JSONParser jsonParser = new JSONParser();
							Object obj = jsonParser.parse(result);
								JSONObject creator = (JSONObject) obj;
								JSONObject all = (JSONObject) creator.get("Creator");
								JSONObject UserBio = (JSONObject) all.get("UserBios");
								Bios.put("Name", UserBio.get("Name").toString());
								Bios.put("Birthday", UserBio.get("Birthday").toString());
								Bios.put("Gender", UserBio.get("Gender").toString());
								JSONObject Likes = (JSONObject) UserBio.get("Like");
								ULikes.put("Food",  GetRes(Likes, "Food"));
								ULikes.put("Game",  GetRes(Likes, "Game"));
								ULikes.put("Watch",  GetRes(Likes, "Watch"));
								ULikes.put("Others", GetRes(Likes, "Others"));
								JSONObject Dislikes = (JSONObject) UserBio.get("Dislike");
								UDlikes.put("Food", GetRes(Dislikes, "Food"));
								UDlikes.put("Others", GetRes(Dislikes, "Others"));
								PerName.addAll(0, GetRes(UserBio, "Prefered Name"));				
								//Personal info all loaded, now moving on to Connections
								JSONObject Conn = (JSONObject) creator.get("Connections");
								JSONObject Fam = (JSONObject) Conn.get("Family");
								String[] fam = Fam.toString().split(",");
								for(int p=0; p<fam.length; p++) {
									int a = 0;
									Family.put(fam[p].split(":")[a].replaceAll("[^a-zA-Z ]", ""), 
											fam[p].split(":")[a+1].replaceAll("[^a-zA-Z ]", ""));
								}		
								JSONObject friend = (JSONObject) Conn.get("Friends");
								String[] fri = friend.toString().split(",");
								for(int t=0; t<fri.length; t++) {
									int a = 0;
									Friends.put(fri[t].split(":")[a].replaceAll("[^a-zA-Z ]", ""), 
											Arrays.asList(fri[t].split(":")[a+1].replaceAll("[^a-zA-Z _]", "")
													.split("_")));
								}
								Evamem.close();
					} catch (FileNotFoundException e1) {
						System.out.println("*Memory data loading error*");
					}  
					
				} 
				public static List<String> GetRes(JSONObject JObj, String Key) {
					List<String> Value = Arrays.asList(JObj.get(Key).toString().split(","));
					for(int a=0; a<Value.size(); a++) {
						String clean = Value.get(a).replaceAll("[^a-zA-Z0-9-_ ]", "");
						Value.set(a, clean);
					}
					return Value;
				}
		//Log creator
		public static void Logger() throws IOException {	
			Loger = true;	 		
			String LogName = "IslaLog" + currentDate;  
			File Log = new File(LogPath + LogName + ".txt");		
		
			if(!Log.exists()) {
		      Log.createNewFile();
		    }
			
			String logg = "";
			
			if(intentF == 0) {
				logg = "statment";
			} else if(intentF == 1) {
				logg = "question";
			}
			
			FileWriter log = new FileWriter(Log, true);
			BufferedWriter logging = new BufferedWriter(log);
			logging.write(currentTime + ": User---{" + UserQ + "}" + "\n" + 
					"EmotionP gives a rating of: " + OvEmoEval + "\n" + 
					"Sentence type is calculated to be: " + logg + "\n");
			//For console view
			System.out.println(currentTime + ": User---{" + UserQ + "}" + "\n" + 
					"EmotionP gives a rating of: " + OvEmoEval + "\n" + 
					"Sentence type is calculated to be: " + logg + "\n");
			
			logging.close();
	}
		public static void LoggerR() throws IOException {	
			Loger = true;	   	
			String LogName = "IslaLog" + currentDate;  
			File Log = new File(LogPath + LogName + ".txt");		
		
			if(!Log.exists()) {
		      Log.createNewFile();
		    }
			
			FileWriter log = new FileWriter(Log, true);
			BufferedWriter logging = new BufferedWriter(log);
			logging.write(currentTime + ": Isla---{" + IslaRes + "}" + "\n");
			logging.close();
			//Needs to implement this into the program
	}
			
		//Services	
		//Time
		public static void time() {
		    LocalDate today = LocalDate.now();  
		    Date now = new Date();
		    currentDate = today.toString(); 
			//Getting the current date
	    
		    //Getting the current time 
		    SimpleDateFormat formatter= new SimpleDateFormat("HH:mm:ss");
		    Date time = new Date(System.currentTimeMillis());
		    currentTime = formatter.format(time).toString();
		    
		    String[] timePro = currentTime.split(":");
		    String resTime = timePro[0] + ":" + timePro[1];
		    		    
		    Random ran = new Random();
		    
		    TimeAn = EzRes.get("time").get(ran.nextInt(EzRes.get("time").size()));
		    TimeAn = TimeAn.replace("%time", resTime);
		    
		    DateAn = EzRes.get("date").get(ran.nextInt(EzRes.get("date").size()));
		    DateAn = DateAn.replace("%date", currentDate);
		    
		    SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");
		    DateAn = DateAn.replace("%week", simpleDateformat.format(now));

		    Time = true;
		  }  
		//weatherAPI
		public static void weather() throws ParseException {
			 
		      String API_KEY = "4a515dfc797b53869373dd8e802726cf";
		      //Geo Coord of Kingston
		      String lat = "44.225276"; 
		      String lon = "-76.495083";
		      //Geo Coord of Fredericton
		      //String lat = "45.963589";
		      //String lon = "-66.643112";
		      String part = "minutely";
		      String urlString = "https://api.openweathermap.org/data/2.5/onecall?lat=" + lat + "&lon=" 
		      + lon + "&" + "units=metric&exclude=" + part + "&appid=" + API_KEY;   		
		      
		      try{
		          StringBuilder result = new StringBuilder();
		          URL url = new URL(urlString);
		          URLConnection conn = url.openConnection();
		          BufferedReader rd = new BufferedReader(new InputStreamReader (conn.getInputStream()));
		          String line;
		          while ((line = rd.readLine()) != null){
		              result.append(line);
		          }
		          rd.close();

		          JSONParser jsonParser = new JSONParser();
					Object obj = jsonParser.parse(result.toString());
						JSONObject main = (JSONObject) obj;
						JSONObject current = (JSONObject) main.get("current");
						JSONArray weather = (JSONArray) current.get("weather");
						JSONObject mainW = (JSONObject) weather.get(0);
						
						double wind = (double) current.get("wind_speed");
						double temp = (double) current.get("temp");
						long curID = (long) mainW.get("id");
						
						weatherData.add((int)Math.round(temp));
						weatherData.add((int)Math.round(wind));
						
						JSONArray hourly = (JSONArray) main.get("hourly");
						
						List<Integer> tempDay = new ArrayList<Integer>(); 
						List<Integer> windDay = new ArrayList<Integer>();
						List<Long> id = new ArrayList<Long>();
						
						for(int o=0; o<8; o++) {
							JSONObject hour = (JSONObject) hourly.get(o);
							
							Object t = hour.get("temp");
							double tempD = ((Number) t).doubleValue();
							tempDay.add((int)tempD);
							
							Object w = hour.get("wind_speed");
							double windD = ((Number) w).doubleValue();
							windDay.add((int)windD);
							
							JSONArray idWeather = (JSONArray) hour.get("weather");
							JSONObject idA = (JSONObject) idWeather.get(0);
							id.add((Long) idA.get("id"));
						}			
						//checking the type of weather using code lables provided by the API
						Boolean rain = false;
						Boolean Snow = false;
						Boolean cloudy = false;
						Boolean sunny = false;
						if(curID >= 200 && curID < 250) {
							rain = true;
						} else if(curID >= 300 && curID < 350) {
							rain = true;
						} else if(curID >= 500 && curID < 550) {
							rain = true;
						} else if(curID >= 600 && curID < 650) {
							Snow = true;
						} else if(curID == 800) {
							sunny = true;
						} else if(curID >= 800 && curID < 805){
							cloudy = true;
						}
						WeatherNow.put("raining", rain);
						WeatherNow.put("snowing", Snow);
						WeatherNow.put("cloudy", cloudy);
						WeatherNow.put("sunny", sunny);
						//checking the type of weather using code lables provided by the API
						int Fhot = 0;
						int Fcold = 0;		
						int Frain = 0;
						int Fsnow = 0;
						int Fcloudy = 0;
						int Fwind = 0;
						
						for(int g=0; g<tempDay.size(); g++) {
							if(tempDay.get(g) > 25) {
								Fhot++;
							} else if(tempDay.get(g) < 15) {
								Fcold++;
							}

							if(id.get(g) >= 200 && id.get(g) < 250) {
								Frain++;
							} else if(id.get(g) >= 300 && id.get(g) < 350) {
								Frain++;
							} else if(id.get(g) >= 500 && id.get(g) < 550) {
								Frain++;
							} else if(id.get(g) >= 600 && id.get(g) < 650) {
								Fsnow++;
							} else if(id.get(g) >= 800 && id.get(g) < 805) {
								Fcloudy++;
							}
							
							if(windDay.get(g) > 50) {
								Fwind++;
							}
						}
						
						if(Fhot > Fcold) {
							weatherPro.add("Hot");
						} else if(Fhot < Fcold){
							weatherPro.add("Cold");
						} else {
							weatherPro.add("Comfortable");
						}

						HashMap<Integer, String> weathertag = new HashMap<Integer, String>();
						weathertag.put(Frain, "Rainy");
						weathertag.put(Fsnow, "Snowy");
						weathertag.put(Fwind, "Windy");
						weathertag.put(Fcloudy, "Cloudy");
						List<Integer> comp = new ArrayList<Integer>();
						comp.add(Frain);
						comp.add(Fsnow);
						comp.add(Fcloudy);
						comp.add(Fwind);
						int key = Collections.max(comp);
						weatherTag = weathertag.get(key);
						/*weatherTag is the weather type of the day that may need attention 
						 * determined by seeing what weather tag appears more during the day
						 */
						
						/*function to go through the outputs above are below,
						 * it will output using preset answer formats from 
						 * EzResWeather HashMap 
						 */
						
						//code
						String temwin = EzResWeather.get("Answer").replaceAll("%temp", weatherData.get(0).toString() + "°C");	
						temwin = temwin.replaceAll("%wind", weatherData.get(1).toString() + " m/s");	
						
						WeatherAn = (EzResWeather.get(weatherPro.get(0)) +
											"\n" + temwin + 
											"\n" + EzResWeather.get(weatherTag));
						//code

		          Weather = true;
		      }catch (IOException e){
		          System.out.println(e.getMessage());
		          Weather = false;
		      }  
		   }
		//Oxford Dictionary
		public static void Dictionary(){
				final String language = "en-us";
	            String word = DictionWord;
	            final String fields = "definitions";
	            final String strictMatch = "false";
	            final String word_id = word.toLowerCase();
	            JSONParser jsonParser = new JSONParser();

			        //replace with your own app id and app key
			        final String app_id = "65a1afcb";
			        final String app_key = "d6878e4c3cab2e776a455cf1ab389ace";
			        try {
			            URL url = new URL("https://od-api.oxforddictionaries.com:443/api/v2/entries/" 
			        + language + "/" + word_id + "?" + "fields=" + fields + "&strictMatch=" + strictMatch);
			            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
			            urlConnection.setRequestProperty("Accept","application/json");
			            urlConnection.setRequestProperty("app_id",app_id);
			            urlConnection.setRequestProperty("app_key",app_key);

			            // read the output from the server
			            BufferedReader reader = new BufferedReader
			            		(new InputStreamReader(urlConnection.getInputStream()));
			            StringBuilder stringBuilder = new StringBuilder();

			            String line = null;
			            while ((line = reader.readLine()) != null) {
			                stringBuilder.append(line + "\n");
			            }
			            String result = stringBuilder.toString();    
			            Object obj = jsonParser.parse(result);
			                JSONObject js = (JSONObject) obj;
			                JSONArray results = (JSONArray) js.get("results");
			                
			                List<String> defs = new ArrayList<String>();
			                
			                 for(int i = 0; i < results.size(); i++){
			                     JSONObject lentries = (JSONObject) results.get(i);
			                     JSONArray la = (JSONArray) lentries.get("lexicalEntries");
			                     
			                     for(int j=0;j<la.size();j++){
			                         JSONObject entries = (JSONObject) la.get(j);
			                         JSONArray e = (JSONArray) entries.get("entries");

			                         for(int t = 0;t < e.size();t++){
			                             JSONObject senses = (JSONObject) e.get(t);
			                             JSONArray s = (JSONArray) senses.get("senses");
			                             JSONObject d = (JSONObject) s.get(0);
			                             JSONArray de = (JSONArray) d.get("definitions");
			                             finalDef = de.toJSONString();
			                             finalDef = finalDef.replaceAll("[^a-zA-Z0-9 ,]", "");
			                             
										defs.add(finalDef);
										
										System.out.println(defs);
			                         }
			                     }
			                 }   
			                
			                Random ran = new Random();
			                String defini = defs.get(0);
							DictionRes = EzRes.get("dictionary").get(ran.nextInt(EzRes.get("dictionary").size()));;
							DictionRes = DictionRes.replace("%word", DictionWord);
							DictionRes = DictionRes.replace("%definition", defini);
			        }
			        catch (Exception e) {
			            e.printStackTrace();
			        }
			    }

}



        
		
