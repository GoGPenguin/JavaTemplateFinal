import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayList;

public class Main extends JFrame {
    public static void main(String[] args) {
        new Main();
    }

    JLabel importLbl = new JLabel("Import data");
    JLabel keywordLbl = new JLabel("Keyword");
    JTextField fileTxt = new JTextField();
    JTextField nameTxt = new JTextField();
    JButton importBtn = new JButton("Import file");
    JButton rankingBtn = new JButton("Ranking");
    JButton searchBtn = new JButton("Search");
    JButton wonBtn = new JButton("Won teams");
    JTextArea showDataTxtA = new JTextArea();



    public Main() {
        this.setSize(400, 300);
        this.setTitle("Quan ly ket qua ICPC");
        this.setDefaultCloseOperation(3);
        this.setLayout(null);

        importLbl.setBounds(10, 50, 80, 30);
        keywordLbl.setBounds(10, 90, 80, 30);
        rankingBtn.setBounds(10, 130, 80, 30);
        searchBtn.setBounds(140, 130, 80, 30);
        nameTxt.setBounds(140, 90, 200, 30);
        fileTxt.setBounds(140, 50, 80, 30);
        importBtn.setBounds(230, 50, 80, 30);
        wonBtn.setBounds(230, 130, 80, 30);
        showDataTxtA.setBounds(10, 170, 360, 80);

        this.add(importLbl);
        this.add(keywordLbl);
        this.add(rankingBtn);
        this.add(searchBtn);
        this.add(nameTxt);
        this.add(fileTxt);
        this.add(importBtn);
        this.add(wonBtn);
        this.add(showDataTxtA);

        importBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ReadDataFromTextFile();
                ReadDataFromDTB();
            }
        });

        rankingBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ShowRanking();
            }
        });

        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ShowRanking();
            }
        });

        wonBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showWonTeam();
            }
        });

        this.setVisible(true);
    }

    public void WriteDataFromFile(String[] data) {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String URL = "jdbc:sqlserver://localhost:1433;databaseName=FinalJava;integratedSecurity=true;trustServerCertificate=true";
            Connection conn = DriverManager.getConnection(URL);
            Statement st = conn.createStatement();

            String query = "INSERT INTO ICPC VALUES (" +
                    "'" + data[0] + "'," +
                    "'" + data[1] + "'," +
                    "'" + data[2] + "'," +
                    "" + data[3] + "," +
                    "'" + data[4] +"'" +
                    ")";
            st.executeUpdate(query);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void WriteToTxtFile(String s) {
        String location = "D:\\Program\\Java-DUT\\De_1_102210020_NguyenVanTruongSon.java\\src\\";
        try {
            FileWriter fileWriter = new FileWriter(location);
            BufferedWriter bWriter = new BufferedWriter(fileWriter);

            bWriter.write(s);
            bWriter.newLine();

            bWriter.close();
            fileWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ReadDataFromTextFile() {

        try {
            String location = "D:\\Program\\Java-DUT\\De_1_102210020_NguyenVanTruongSon.java\\src\\" + fileTxt.getText();
            File file = new File(location);
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String st;
            String[] dataField;

//            String s="hello";
//            System.out.println(s.substring(0,2)); //he

            while((st = reader.readLine()) != null) {
                dataField = st.split(",", -1);
                WriteDataFromFile(dataField);
            }

            reader.close();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void ReadDataFromDTB() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String URL = "jdbc:sqlserver://localhost:1433;databaseName=FinalJava;integratedSecurity=true;trustServerCertificate=true";
            Connection conn = DriverManager.getConnection(URL);
            Statement st = conn.createStatement();

            String query = "SELECT * FROM " + "ICPC";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            showDataTxtA.setText("");
            while(rs.next()) {
                String dataRow = rs.getString(1) + " " +
                                rs.getString(2) + " " +
                                rs.getString(3) + " " +
                                rs.getString(4) + " " +
                                rs.getString(5) + "\n";
                showDataTxtA.append(dataRow);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void ShowRanking() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String URL = "jdbc:sqlserver://localhost:1433;databaseName=FinalJava;integratedSecurity=true;trustServerCertificate=true";
            Connection conn = DriverManager.getConnection(URL);
            Statement st = conn.createStatement();

//            String query = "SELECT TeamName, UniversityName, COUNT(Result), SUM(Time)\n" +
//                    "FROM ICPC\n" +
//                    "WHERE Result = 'AC' ";
//            if (!nameTxt.getText().equals("")) query += " And UniversityName = '" + nameTxt.getText() + "' ";
//            query += "GROUP BY TeamName, UniversityName, Result\n" +
//                    "ORDER BY COUNT(Result) DESC\n";

            String query = "WITH BestTeams AS (\n" +
                    "    SELECT UniversityName, TeamName, COUNT(DISTINCT ProblemID) AS ProblemsSolved, SUM(MinTime) AS TotalTime\n" +
                    "    FROM (\n" +
                    "        SELECT UniversityName, TeamName, ProblemID, MIN(Time) AS MinTime\n" +
                    "        FROM ICPC\n" +
                    "        WHERE Result = 'AC'\n" +
                    "        GROUP BY UniversityName, TeamName, ProblemID\n" +
                    "    ) AS FirstSolved\n" +
                    "    GROUP BY UniversityName, TeamName\n" +
                    "),\n" +
                    "BestUniversityTeams AS (\n" +
                    "    SELECT UniversityName, MAX(ProblemsSolved) AS MaxProblemsSolved, MIN(TotalTime) AS MinTotalTime\n" +
                    "    FROM BestTeams\n" +
                    "    GROUP BY UniversityName\n" +
                    "),\n" +
                    "Ranking AS (\n" +
                    "    SELECT UniversityName, TeamName, ProblemsSolved, TotalTime,\n" +
                    "           DENSE_RANK() OVER (ORDER BY ProblemsSolved DESC, TotalTime ASC) AS TeamRank\n" +
                    "    FROM BestTeams\n" +
                    ")\n" +
                    "SELECT UniversityName, TeamName, ProblemsSolved, TotalTime, TeamRank\n" +
                    "FROM Ranking\n";
                    if (!nameTxt.getText().equals("")) query += "UniversityName = '" + nameTxt.getText() + "' ";
                    query += "ORDER BY TeamRank ASC\n";



            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            showDataTxtA.setText("");
            while(rs.next()) {
                String dataRow = rs.getString(5) + " " +
                        rs.getString(1) + " " +
                        rs.getString(2) + " " +
                        rs.getString(3) + " " +
                        rs.getString(4) + " " + "\n";
                showDataTxtA.append(dataRow);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void showWonTeam() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String URL = "jdbc:sqlserver://localhost:1433;databaseName=FinalJava;integratedSecurity=true;trustServerCertificate=true";
            Connection conn = DriverManager.getConnection(URL);
            Statement st = conn.createStatement();

            String query = "WITH BestTeams AS (\n" +
                    "    SELECT UniversityName, TeamName, COUNT(DISTINCT ProblemID) AS ProblemsSolved, SUM(MinTime) AS TotalTime\n" +
                    "    FROM (\n" +
                    "        SELECT UniversityName, TeamName, ProblemID, MIN(Time) AS MinTime\n" +
                    "        FROM ICPC\n" +
                    "        WHERE Result = 'AC'\n" +
                    "        GROUP BY UniversityName, TeamName, ProblemID\n" +
                    "    ) AS FirstSolved\n" +
                    "    GROUP BY UniversityName, TeamName\n" +
                    "),\n" +
                    "BestUniversityTeams AS (\n" +
                    "    SELECT UniversityName, MAX(ProblemsSolved) AS MaxProblemsSolved, MIN(TotalTime) AS MinTotalTime\n" +
                    "    FROM BestTeams\n" +
                    "    GROUP BY UniversityName\n" +
                    "),\n" +
                    "Ranking AS (\n" +
                    "    SELECT BestTeams.UniversityName, TeamName, ProblemsSolved, TotalTime,\n" +
                    "           RANK() OVER (PARTITION BY BestTeams.UniversityName ORDER BY ProblemsSolved DESC, TotalTime ASC) AS TeamRank,\n" +
                    "           DENSE_RANK() OVER (ORDER BY MaxProblemsSolved DESC, MinTotalTime ASC) AS UniversityRank\n" +
                    "    FROM BestTeams\n" +
                    "    JOIN BestUniversityTeams\n" +
                    "    ON BestTeams.UniversityName = BestUniversityTeams.UniversityName\n" +
                    ")\n" +
                    "SELECT UniversityName, TeamName, ProblemsSolved, TotalTime, UniversityRank\n" +
                    "FROM Ranking\n" +
                    "WHERE TeamRank = 1\n";


            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            showDataTxtA.setText("");
            while(rs.next()) {
                String dataRow = rs.getString(5) + " " +
                        rs.getString(1) + " " +
                        rs.getString(2) + " " +
                        rs.getString(3) + " " +
                        rs.getString(4) + " " + "\n";
                showDataTxtA.append(dataRow);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}