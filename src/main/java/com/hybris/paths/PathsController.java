package com.hybris.paths;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;  
import java.text.SimpleDateFormat;  
import java.util.Date;  
import java.util.Calendar;  

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.*;

@Controller
public class PathsController {

	@Autowired
	MaterialUtil mu;

	int idCount = -1;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String  getjson(Model model) throws ParseException, IOException, JSONException {
		String json = getJson();				
		model.addAttribute("json", json);
		return "indexangular";
	}

	@RequestMapping(value = "/save_Json", method = RequestMethod.POST)
	public String saveJson(@RequestBody String newJson) throws ParseException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		Object json = mapper.readValue(newJson, Object.class);	
		String s = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);	 
		File file = new File( getClass().getClassLoader().getResource("alltags.json").getFile());
		Path path = file.getParentFile().getParentFile().getParentFile().toPath().resolve("src/main/resources/alltags.json");
		System.out.println("New Json: "+newJson );

		System.out.println("New Json Path: "+path.resolve("resources") );
		Files.write(path, s.getBytes());		

		Date date = Calendar.getInstance().getTime();  
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");  
		String strDate = dateFormat.format(date);  
		path = file.getParentFile().getParentFile().getParentFile().toPath().resolve("src/main/resources/archived/alltag"+strDate+"s.json");
		Files.write(path, s.getBytes());		
		return "indexangular";
	}

	@RequestMapping(value = "/get_DetailsFromURL", method = RequestMethod.POST)
	public ResponseEntity<String> getDetailsFromURL(Model model, @RequestBody String url) throws Exception {
		getJson();
		String json = mu.get(url, ++idCount);
		model.addAttribute("newMateral", json);		
		return ResponseEntity.ok(json);
	}

	private void setMaxId( String json ) throws JSONException, IOException {
		JSONArray jsonArray = new JSONArray(json);		
		Integer id;
		for (int i = 0; i < jsonArray.length(); i++)
		{			
		     id = (Integer)jsonArray.getJSONObject(i).getInt("ID");
		     if (id > idCount)
		    	 	idCount=id;
		}
	}


	private String getJson() throws IOException, JSONException {
		File file = new File( getClass().getClassLoader().getResource("alltags.json").getFile());
		Path path = Paths.get(file.getPath());
		String json = new String(Files.readAllBytes(path));
		setMaxId(json);
		return json;
	}

}
