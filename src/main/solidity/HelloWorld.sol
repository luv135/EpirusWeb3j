pragma solidity >=0.4.21 <0.7.0;

// Modified Greeter contract. Based on example at https://www.ethereum.org/greeter.

contract Mortal {
    /* Define variable owner of the type address*/
    address owner;

    /* this function is executed at initialization and sets the owner of the contract */
    constructor () public { owner = msg.sender; }

    /* Function to recover the funds on the contract */
    function kill() public { if (msg.sender == owner) selfdestruct(msg.sender); }
}

contract HelloWorld is Mortal {
    /* define variable greeting of the type string */
    string greet;

    /* this runs when the contract is executed */
     constructor (string memory _greet) public {
         greet = _greet;
     }

    function newGreeting(string memory _greet) public {
        greet = _greet;
    }

    /* main function */
    function greeting() public view returns (string memory)  {
        return greet;
    }
}
