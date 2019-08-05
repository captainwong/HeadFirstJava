import java.util.*;

/**
 * DotComBust
 */
public class DotComBust {

    private GameHelper helper = new GameHelper();
    private ArrayList<DotCom> dotComs = new ArrayList<>();
    private int numOfGuesses = 0;

    private void setupGame() {
        dotComs.add(new DotCom("Pets.com"));
        dotComs.add(new DotCom("eToys.com"));
        dotComs.add(new DotCom("Go2.com"));

        System.out.println("Your goal is to sink three dot coms.");
        System.out.println("Pets.com, eToys.com, Go2.com");
        System.out.println("Try to sink them all in the fewest number of guesses");

        for (DotCom dotCom : dotComs) {
            dotCom.setLocationCells(helper.placeDotCom(3));
        }
    }

    private void startPlaying(){
        while(!dotComs.isEmpty()){
            checkUserGuess(helper.getUserInput("Enter a guess"));    
        }
        finishGame();
    }

    private void checkUserGuess(String userGuess){
        numOfGuesses++;
        String result = "miss";
        for(DotCom dotCom : dotComs){
            result = dotCom.checkYourself(userGuess);
            if(result.equals("hit")){break;}
            if(result.equals("kill")){
                dotComs.remove(dotCom);
                break;
            }
        }
        System.out.println(result);
    }

    private void finishGame(){
        System.out.println("All Dot Coms are dead! Your stock is now worthless.");
        if(numOfGuesses<=18){
            System.out.println("It only took you " + numOfGuesses + " guesses.");
            System.out.println("You got out before your options sank.");
        }else{
            System.out.println("Took you long enough. " + numOfGuesses + " guesses.");
            System.out.println("Fish are dancing with your options");
        }
    }

    public static void main(String[] args) {
        DotComBust game = new DotComBust();
        game.setupGame();
        game.startPlaying();
    }
}
