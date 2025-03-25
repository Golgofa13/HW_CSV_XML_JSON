package ru.netogy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, "C:\\Users\\glaz6\\IdeaProjects\\HW_CSVtoJSON\\CSVtoJSON.json");
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

