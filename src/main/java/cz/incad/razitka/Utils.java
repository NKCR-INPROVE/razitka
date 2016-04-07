package cz.incad.razitka;

import org.aplikator.server.ListRegistry;
import org.aplikator.server.descriptor.ListProvider;

public class Utils {
    
    //public static final String dateFormat = "dd.mm.yyyy";

    static public ListProvider namedList(DLists listEntity,Enum<?> listName) {
    	ListProvider listProvider = ListRegistry.get().getListProvider(listName.name());
    	if (listProvider==null) {
    		listProvider = new DListProvider(listEntity,listName);
    		//Structure.listProviders.put(listName, listProvider);
    	}
    	return listProvider;
    }           
}
