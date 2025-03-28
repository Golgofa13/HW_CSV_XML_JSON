package ru.netogy;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, "E:\\GIT\\HW_CSVtoJSON\\data.json");
        String fileName2 = "data.xml";
        List<Employee> list2 = parseXML(columnMapping, fileName2);
        String json2 = listToJson(list2);
        writeString(json2,"E:\\GIT\\HW_CSVtoJSON\\data2.json");
    }

    private static List<Employee> parseXML(String[] columnMapping, String fileName2) {
        List<Employee> employees = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(fileName2));

            Element root = doc.getDocumentElement();

            NodeList nodeList = root.getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    int id = Integer.parseInt(element.getElementsByTagName("id").item(0).getTextContent());
                    String firstName = element.getElementsByTagName("firstName").item(0).getTextContent();
                    String lastName = element.getElementsByTagName("lastName").item(0).getTextContent();
                    String country = element.getElementsByTagName("country").item(0).getTextContent();
                    int age = Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent());
                    Employee employee = new Employee(id, firstName, lastName, country, age);
                    employees.add(employee);
                }
            }
        } catch (Exception e) {
            {
                e.printStackTrace();
            }
        }
        return employees;
    }

    public static void writeString(String content, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            // Записываем строку в файл
            writer.write(content);

            // Очистка буфера
            writer.flush();

            System.out.println("JSON успешно записан в файл: " + filePath);

        } catch (IOException e) {
            System.err.println("Ошибка при записи в файл: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String listToJson(List<Employee> list) {
        // Создаем GsonBuilder
        GsonBuilder gsonBuilder = new GsonBuilder();

        // Создаем Gson с настроенными опциями
        Gson gson = gsonBuilder.setPrettyPrinting().create();

        // Определяем тип списка
        Type listType = new TypeToken<List<Employee>>(){}.getType();

        // Конвертируем список в JSON строку
        return gson.toJson(list, listType);
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (FileReader fileReader = new FileReader(fileName);
             CSVReader csvReader = new CSVReader(fileReader)) {

            // Создаем стратегию маппинга колонок
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            // Создаем CsvToBean с помощью билдера
            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();

            // Парсим CSV и получаем список сотрудников
            return csvToBean.parse();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

