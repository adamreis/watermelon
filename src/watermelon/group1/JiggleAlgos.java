package watermelon.group1;

import watermelon.group1.Solution;

public class JiggleAlgos {

	
	public static Solution dumbJiggle(Solution baseSolution) {
		Solution newSolution = baseSolution.deepDuplicate();
		newSolution.jiggleAlgo = "dumbJiggle";
		
		// JIGGLE
		
		return newSolution;
	}
}