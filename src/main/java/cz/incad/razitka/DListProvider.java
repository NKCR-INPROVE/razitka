package cz.incad.razitka;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aplikator.client.shared.data.ListItem;
import org.aplikator.server.AplikatorServiceServer;
import org.aplikator.server.Context;
import org.aplikator.server.data.Record;
import org.aplikator.server.descriptor.ListProvider;
import org.aplikator.server.descriptor.Property;
import org.aplikator.server.descriptor.SortItem;
import org.aplikator.server.descriptor.View;

public class DListProvider extends ListProvider.Default {
	private Enum<?> listName;
	private DLists listEntity;
	private View view;
	private Map<String,List<ListItem>> listValues;
	private Map<Enum<?>,Property<String>> localeProperties;
	public enum locales { 
		cz , de ,en ,fr 
	};
	
	private void init() {
		listValues = new HashMap<String, List<ListItem>>();
		for (locales loc : locales.values()) {
   			List<ListItem> list = new ArrayList<ListItem>();
   			listValues.put(loc.name(), list);
   		}
		localeProperties = new HashMap<Enum<?>, Property<String>>();
		
	}
	
	public DListProvider(DLists listEntity, Enum<?> listName) {
		init();
		this.listName = listName;
		this.listEntity = listEntity;
		String listViewId = listEntity.getId()+"listView"+listName.name();	
			view = new View(listEntity, listViewId);
			view.addProperty(listEntity.classType);
			view.addProperty(listEntity.value);
			view.addProperty(listEntity.cz);
			view.addProperty(listEntity.de);
			view.addProperty(listEntity.en);
			view.addProperty(listEntity.fr);
            view.addSortDescriptor(listName.name(), listName.name(), SortItem.ascending(listEntity.poradi));
			view.addQueryDescriptor(listName.name(), listName.name(), listEntity.classType.EQUAL(listName.name()));
		localeProperties.put(locales.cz, listEntity.cz);
		localeProperties.put(locales.de, listEntity.de);
		localeProperties.put(locales.en, listEntity.en);
		localeProperties.put(locales.fr, listEntity.fr);		
	}
	
	public void refreshListValues(Context ctx) {
       		for (String loc : listValues.keySet()) {
       			listValues.get(loc).clear();
       		}
			AplikatorServiceServer aplService =  ctx.getAplikatorService();
	       	List<Record> records = aplService.getRecords(view, listEntity.classType.EQUAL(listName.name()), new SortItem[]{SortItem.ascending(listEntity.poradi)},null, null, null, 0, 0, ctx);
	       	for (Record record : records) {
	       		for (locales loc : locales.values()) {
	       			ListItem listItem = new ListItem.Default( record.getValue(listEntity.value), record.getValue(localeProperties.get(loc)));
	       			listValues.get(loc.name()).add(listItem);
	       		}
	       	}				
	}	
	
	@Override
	public List<ListItem> getListValues(Context ctx) {
		String country = ctx.getUserLocale().getLanguage();
		if ("cs".equalsIgnoreCase(country)) {
			country = "cz";
		}
		
		List<ListItem> list = listValues.get(country);
		if (list==null) {
			list = listValues.get("cz");
			country="cz";
		}
		if (list.size()==0) {
			refreshListValues(ctx);
			list = listValues.get(country);
		}		
		return list;
	}
}
