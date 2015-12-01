package cz.incad.razitka;

import org.aplikator.server.descriptor.ListProvider;

import cz.incad.razitka.server.Structure;

public class Utils {
    
    //public static final String dateFormat = "dd.mm.yyyy";

    static public ListProvider namedList(DLists listEntity,Enum<?> listName) {
    	ListProvider listProvider = Structure.listProviders.get(listName);
    	if (listProvider==null) {
    		listProvider = new DListProvider(listEntity,listName);
    		Structure.listProviders.put(listName, listProvider);
    	}
    	return listProvider;
    }           
}
