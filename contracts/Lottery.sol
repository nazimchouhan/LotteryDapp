// SPDX-License-Identifier: UNLICENSED
pragma solidity >=0.7.0 <0.9.0;

contract Lottery{
    address public manager;
    address payable[] public participants;

    constructor(){
        manager=msg.sender;// global variable 
    }

    receive() external payable {
        require(msg.value==2 ether);
        participants.push(payable(msg.sender)); // array of payables
        
    }
    function getBalance() public view returns(uint){
        require(msg.sender==manager);
        return address(this).balance;
    }

    function random() public view returns(uint){
        bytes32  hash=keccak256(abi.encodePacked(block.difficulty,block.timestamp,participants.length,msg.sender));
     
        return uint(hash);
    }

    function selectWinner() public {
        require(msg.sender==manager);
        require(participants.length>=3);
        uint r=random();
        address payable winner;
        uint index=r% participants.length; // random number
        winner= participants[index];// winner 
        winner.transfer(getBalance());
        participants=new address payable[](0);
    }
}
