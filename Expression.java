import java.util.*;

public class Expression extends ExpressionTree {
   BNode<String> node;
   BNode<String> node1;
   BNode<String> node2;
   StringBuilder expression = new StringBuilder();
   
   public String fullyParenthesized() {
      if(this.root != null){ //if the root is not null call a method to traverse the tree and fully parenthesize it to be returned
         return traverse(this.root);
      }
      return ""; //if the root is null return a empty string
   }
   
   public String traverse(Node<String> root){
      if(isLeaf(root)){ //if the root is a leaf(meaning it is a operand) append to the stringbuilder
         expression.append(root.getData());  
      }
      else{ //if the root is not a leaf(meaning it is a operator) traverse the left and right subtree until you reach a leaf
         expression.append("("); //append a ( before retreiving the left leaf, right leaf and operator
         traverse(((BNode<String>) root).getLeft());
         expression.append(root.getData());
         traverse(((BNode<String>) root).getRight());
         expression.append(")"); //append a ) to close the operation
      }
      return expression.toString(); //return fullyparenthesized form back to original method to be printed
   }

   public Expression(String s) {
      Stack<BNode<String>> stack = new Stack<>();
      s = s.replace(" ", ""); //remove orginal whitespace to not create duplicate whitespace
      s = addSpace(s); //add a indicator where to add whitespace for string split
      s = s.replace(".", " "); //add whitespace where the indicator was added
      String[] ex = s.split(" "); //split the string into parts
      String[] post = Infix(ex); //turn infix expression into postfix by calling another method

      for(int i = 0; i<post.length; i++){
         if(!isOperator(post[i])){
            node = new BNode<String>(post[i], null, null, null); //if T is not a operator push onto stack as a node
            stack.push(node);
         }
         else{
            node = new BNode<String>(post[i], null, null, null); //if T is a operator remove two nodes from the stack and set as left and right leaf
            node1 = stack.pop(); //pop the stack and set it equal to a node
            node2 = stack.pop(); //pop the stack again and set equal to a node
            node.setRight(node1); //then set the right of the operator node as the first node popped
            node.setLeft(node2); //then set the left of the operator node as the second node popped
            stack.push(node); //push the tree onto the stack
         }
      }
      node = stack.pop(); //pop the top node of the stack
      while(!stack.isEmpty()){
         node = stack.pop();  
      }
      this.root = node; //set the root of an expression tree equal to the last node popped off the stack
   }
   
   public double evaluate() {
      // add implementation here
      return eval(this.root);
   }
   
   public double eval(Node<String> root){
      double output;
      if(root == null){ //if the root is null return 0
         return 0;
      }
      else{
         if(isOperator(root.getData()) == false){ //if it is an operand convert the data in the node to a double and return it
            return Double.valueOf(root.getData()); 
         }
         else{ //traverse the tree
            String operator = root.getData(); //if it is an operator set variable "operator" to the string value of the operator in the node
            double left = Double.valueOf(eval(((BNode<String>) root).getLeft())); //retreive the left and right leafs of the tree
            double right = Double.valueOf(eval(((BNode<String>) root).getRight()));
            output = compute(operator, left, right); //call another method to do the computation of the operator and operands returned 
         }
      }
      return output; //return the output
   }
   
   public double compute(String operator, double left, double right){
      double output = 0;
      if(operator.equals("+")){ //if the operator is a + add the two doubles
         output = left + right;
      }
      if(operator.equals("-")){ //if the operator is a - subtract the two doubles
         output = left - right;
      }
      if(operator.equals("*")){ //if the operator is a * multiply the two doubles
         output = left * right;
      }
      if(operator.equals("/")){ //if the operator is a / divide the two doubles
         output = left / right;
      }  
      return output; //return the output
   }
   
   public String addSpace(String s){ //add indicators for where to add whitespace
      if(s.contains("-")){
         s = s.replaceAll("-", ".-.");
      }
      if(s.contains("+")){
         s = s.replace("+", ".+.");
      }
      if(s.contains("*")){
         s = s.replace("*", ".*.");
      }
      if(s.contains("/")){
         s = s.replace("/", "./.");
      }
      if(s.contains("(")){
         s = s.replace("(", "(.");  
      }
      if(s.contains(")")){
         s = s.replace(")", ".)");  
      }
      return s; //return the changed string
   }
   
   public boolean isOperator(String c){ //check if the character is a operator 
      if(c.equals("-") || c.equals("*") || c.equals("+") || c.equals("/")){
         return true;  
      }
      return false;
   }
   
   public boolean isParentheses(String c){ //check if the character is a parentheses
      if(c.equals("(") || c.equals(")")){
         return true;  
      }
      return false;
   }
   
   public String[] Infix(String[] ex){ //turn infix into postfix
      Stack<String> s = new Stack<String>();
      ArrayList<String> word = new ArrayList<>();
      for(int i = 0; i<ex.length; i++){ 
         if(isOperator(ex[i]) == false && isParentheses(ex[i]) == false){ //if the string is not a operator or parentheses add it to the arraylist 
            word.add(ex[i]);
         }
         else if(ex[i].equals("(")){ //if the string is a open parenthesis add it to the stack
            s.push(ex[i]);  
         }
         else if(ex[i].equals(")")){ //if the string is a closing parenthesis pop the stack until you encounter a open parenthesis or if the stack is not empty
            while(!s.peek().equals("(") && !s.isEmpty()){
               word.add(s.pop()); //add operators in the stack to the arraylist
            }
            s.pop(); //pop/remove the open parenthesis from the stack
         }
         else if(isOperator(ex[i]) == true){
            if (s.isEmpty() || outPrec(ex[i]) > inPrec(s.peek())) { //if operator outside the stack has a higher precedence than the one 
               s.push(ex[i]);                                       //inside the stack we add the one outside to the arraylist
            } 
            else{
               while(!s.isEmpty() && outPrec(ex[i]) < inPrec(s.peek())){ //if the operator outside the stack has a smaller precedence then we
                  word.add(s.pop());                                    //pop the operator from the stack and add it to arraylist
               }                                                         //operator inside the stack that has a smaller precedence or until the stack is empty. 
               s.push(ex[i]);                                            //finally we put the operator outside the stack into the stack
            }
         }
      }
      while(!s.isEmpty()){ //if the stack is not empty add the rest of the stack to the arraylist
         word.add(s.pop());
      }
      String[] retword = new String[word.size()];
      for(int j = 0; j<word.size(); j++){ //copy arraylist into a string array
         retword[j] = word.get(j);
      }
      return retword; //return string array
   }
   
   //function to return precedence value 
   //if the operator is inside the stack
   static int inPrec(String input){
      switch (input){ 
        case "+": 
        case "-": 
            return 2; 
        case "*": 
        case "/": 
            return 4; 
      } 
      return 0;  
   } 
  
   // function to return precedence value 
   // if operator is present outside stack. 
   static int outPrec(String input) { 
      switch (input) { 
        case "+": 
        case "-": 
            return 1; 
        case "*": 
        case "/": 
            return 3; 
      } 
      return 0; 
   } 
}
