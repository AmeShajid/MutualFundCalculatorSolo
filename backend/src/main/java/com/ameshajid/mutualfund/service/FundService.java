/**
 FundService provides a hardcoded list of mutual funds.
 When the frontend asks for available funds (dropdown list), this service builds a list of Fund objects and returns it.
 There is no database here  the list is manually created in memory.
 */
package com.ameshajid.mutualfund.service;
//Allows us to use Array
import java.util.ArrayList;
//Allows us to use List
import java.util.List;

//Tells Spring that this is a service component
import org.springframework.stereotype.Service;
// This import allows us to use the Fund model class
import com.ameshajid.mutualfund.model.Fund;

//This class as a service in Spring
@Service
public class FundService {

    //returns list of fund objects
    public List<Fund> getAllFunds() {

        //new empty list
        List<Fund> funds = new ArrayList<>();
        //creates a new fund object + adds to list
        funds.add(new Fund("VSMPX", "Vanguard Total Stock Market Index Fund;Institutional Plus"));
        funds.add(new Fund("FXAIX", "Fidelity 500 Index Fund"));
        funds.add(new Fund("VFIAX", "Vanguard 500 Index Fund;Admiral"));
        funds.add(new Fund("VTSAX", "Vanguard Total Stock Market Index Fund;Admiral"));
        funds.add(new Fund("SPAXX", "Fidelity Government Money Market Fund"));
        funds.add(new Fund("VMFXX", "Vanguard Federal Money Market Fund;Investor"));
        funds.add(new Fund("VGTSX", "Vanguard Total International Stock Index Fund;Investor"));
        funds.add(new Fund("SWVXX", "Schwab Prime Advantage Money Market Fund;Inv"));
        funds.add(new Fund("FDRXX", "Fidelity Government Cash Reserves"));
        funds.add(new Fund("FGTXX", "Goldman Sachs FS Government Fund;Institutional"));
        funds.add(new Fund("OGVXX", "JPMorgan US Government Money Market Fund;Capital"));
        funds.add(new Fund("FCTDX", "Fidelity Strategic Advisers Fidelity US Total Stk"));
        funds.add(new Fund("VIIIX", "Vanguard Institutional Index Fund;Inst Plus"));
        funds.add(new Fund("FRGXX", "Fidelity Instl Government Portfolio;Institutional"));
        funds.add(new Fund("VTBNX", "Vanguard Total Bond Market II Index Fund;Institutional"));
        funds.add(new Fund("MVRXX", "Morgan Stanley Inst Liq Government Port;Institutional"));
        funds.add(new Fund("TFDXX", "BlackRock Liquidity FedFund;Institutional"));
        funds.add(new Fund("GVMXX", "State Street US Government Money Market Fund;Prem"));
        funds.add(new Fund("AGTHX", "American Funds Growth Fund of America;A"));
        funds.add(new Fund("VTBIX", "Vanguard Total Bond Market II Index Fund;Investor"));
        funds.add(new Fund("CJTXX", "JPMorgan 100% US Treasury Securities Money Market Fund;Capital"));
        funds.add(new Fund("TTTXX", "BlackRock Liquidity Treasury Trust Fund;Institutional"));
        funds.add(new Fund("FCNTX", "Fidelity Contrafund"));
        funds.add(new Fund("SNAXX", "Schwab Prime Advantage Money Market Fund;Ultra"));
        funds.add(new Fund("PIMIX", "PIMCO Income Fund;Institutional"));

        return funds;
    }
}