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

    //Fund list is built once and reused for every request
    private final List<Fund> funds;

    //Constructor initializes the fund list once when the application starts
    public FundService() {
        List<Fund> list = new ArrayList<>();
        //creates a new fund object + adds to list
        list.add(new Fund("VSMPX", "Vanguard Total Stock Market Index Fund;Institutional Plus"));
        list.add(new Fund("FXAIX", "Fidelity 500 Index Fund"));
        list.add(new Fund("VFIAX", "Vanguard 500 Index Fund;Admiral"));
        list.add(new Fund("VTSAX", "Vanguard Total Stock Market Index Fund;Admiral"));
        list.add(new Fund("SPAXX", "Fidelity Government Money Market Fund"));
        list.add(new Fund("VMFXX", "Vanguard Federal Money Market Fund;Investor"));
        list.add(new Fund("VGTSX", "Vanguard Total International Stock Index Fund;Investor"));
        list.add(new Fund("SWVXX", "Schwab Prime Advantage Money Market Fund;Inv"));
        list.add(new Fund("FDRXX", "Fidelity Government Cash Reserves"));
        list.add(new Fund("FGTXX", "Goldman Sachs FS Government Fund;Institutional"));
        list.add(new Fund("OGVXX", "JPMorgan US Government Money Market Fund;Capital"));
        list.add(new Fund("FCTDX", "Fidelity Strategic Advisers Fidelity US Total Stk"));
        list.add(new Fund("VIIIX", "Vanguard Institutional Index Fund;Inst Plus"));
        list.add(new Fund("FRGXX", "Fidelity Instl Government Portfolio;Institutional"));
        list.add(new Fund("VTBNX", "Vanguard Total Bond Market II Index Fund;Institutional"));
        list.add(new Fund("MVRXX", "Morgan Stanley Inst Liq Government Port;Institutional"));
        list.add(new Fund("TFDXX", "BlackRock Liquidity FedFund;Institutional"));
        list.add(new Fund("GVMXX", "State Street US Government Money Market Fund;Prem"));
        list.add(new Fund("AGTHX", "American Funds Growth Fund of America;A"));
        list.add(new Fund("VTBIX", "Vanguard Total Bond Market II Index Fund;Investor"));
        list.add(new Fund("CJTXX", "JPMorgan 100% US Treasury Securities Money Market Fund;Capital"));
        list.add(new Fund("TTTXX", "BlackRock Liquidity Treasury Trust Fund;Institutional"));
        list.add(new Fund("FCNTX", "Fidelity Contrafund"));
        list.add(new Fund("SNAXX", "Schwab Prime Advantage Money Market Fund;Ultra"));
        list.add(new Fund("PIMIX", "PIMCO Income Fund;Institutional"));

        //Store as unmodifiable so it can't be accidentally changed
        this.funds = List.copyOf(list);
    }

    //returns the pre-built list of fund objects
    public List<Fund> getAllFunds() {
        return funds;
    }
}