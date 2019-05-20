package com.careeroffice.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;

import com.careeroffice.model.*;
import com.careeroffice.service.AuthService;
import com.careeroffice.service.KeywordService;
import com.careeroffice.service.factory.ServiceEnum;
import com.careeroffice.service.factory.ServiceFactory;
import com.careeroffice.service.pivot.KeywordCvPivotService;
import com.careeroffice.service.CvService;


/**
 * Handles login requests and responses.
 */
@WebServlet({"/StudentUploadCv", "/upload_cv"})
@MultipartConfig(fileSizeThreshold=1024*1024*2, // 2MB
        maxFileSize=1024*1024*10,      // 10MB
        maxRequestSize=1024*1024*50)   // 50MB
public class StudentUploadCv extends HttpServlet {

    /**
     * Java related serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Name of the directory where uploaded files will be saved, relative to
     * the web application directory.
     */
    private static final String SAVE_DIR = System.getProperty("user.home") + "/Desktop/uploads";

    /**
     * An instance of the database connection.
     */
    @Resource(name = "jdbc/career_office")
    private DataSource ds;

    /**
     * Handles all GET requests.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        KeywordService keywordService = (KeywordService) ServiceFactory.getService(ServiceEnum.KeywordService);
        KeywordCvPivotService keywordCvPivotService = (KeywordCvPivotService) ServiceFactory.getService(ServiceEnum.KeywordCvPivotService);
        AuthService authService = new AuthService(request.getSession());
        CvService cvService = (CvService) ServiceFactory.getService(ServiceEnum.CvService);

        User user = authService.getUser();
        String username = user.getUsername();
        Cv cv = cvService.findOne(username);

        List<Keyword> keywords = keywordService.findAll();

        if (cv != null && !cv.getFileUrl().isEmpty()) {
            List<Keyword> checkedKeywords = keywordCvPivotService.findByCvId(cv.getId());
            if (checkedKeywords == null) {
                request.setAttribute("keywords", keywords);
                request.getRequestDispatcher("WEB-INF/views/student/upload_cv.jsp").forward(request, response);
                return;
            }

            List<CheckedUserKeyword> pivotTable = new ArrayList<>();

            for (Keyword keyword: keywords) {
                if (checkedKeywords.contains(keyword)) {
                    pivotTable.add(new CheckedUserKeyword(keyword.getTitle(), keyword.getSlug(), true));
                } else {
                    pivotTable.add(new CheckedUserKeyword(keyword.getTitle(), keyword.getSlug(), false));
                }

            }
            request.setAttribute("pivotTable", pivotTable);
            request.getRequestDispatcher("WEB-INF/views/student/upload_cv.jsp").forward(request, response);
            return;
        }



        request.setAttribute("keywords", keywords);
        request.getRequestDispatcher("WEB-INF/views/student/upload_cv.jsp").forward(request, response);




    }

    /**
     * Handles all POST requests.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        CvService cvService = (CvService) ServiceFactory.getService(ServiceEnum.CvService);
        KeywordService keywordService = (KeywordService) ServiceFactory.getService(ServiceEnum.KeywordService);
        KeywordCvPivotService keywordCvPivotService = (KeywordCvPivotService) ServiceFactory.getService(ServiceEnum.KeywordCvPivotService);
        AuthService authService = new AuthService(request.getSession());

        User user = authService.getUser();
        String username = user.getUsername();

        String[] keywords = request.getParameterValues("keywords");
        Part part = request.getPart("file");

        boolean hasError = false;

        if (keywords == null) {
            request.getSession().setAttribute("keywordsError", true);
            hasError = true;
        }

        if (part.getSubmittedFileName() == "") {
            request.getSession().setAttribute("cvError", true);
            hasError = true;
        }

        if (hasError) {
            doGet(request, response);
            return;
        }

        String fileUrl = savedPathCalculator(username).toString();

        // If CV doesn't exist
        if (cvService.findOne(username) == null) {
            cvService.save(username, fileUrl);
        } else {
            cvService.update(new Cv(username, fileUrl));
        }
        writeFileToDisk(part, username);

        int cvId = cvService.findOne(user.getUsername()).getId();
        keywordCvPivotService.deleteByCvId(cvId);

        for (String keyword: keywords) {
            int keywordId = keywordService.findKeywordByTitle(keyword).getId();
            keywordCvPivotService.save(new KeywordCvPivot(keywordId, cvId));
        }

        request.getRequestDispatcher(user.getRoleId()).forward(request, response);
    }

    private void writeFileToDisk(Part part, String username)
            throws IOException{

        Path savePath = savedPathCalculator(username);

        // Creates the save directory if it does not exists
        File fileSavedDir = new File(SAVE_DIR);

        if (!fileSavedDir.exists()) {
            fileSavedDir.mkdir();
        }

        try (InputStream input = part.getInputStream()) {
            Files.deleteIfExists(savePath);
            Files.copy(input, savePath);

        }
    }

    private Path savedPathCalculator(String username) {
        String fileName = username + ".pdf";
        return new File(SAVE_DIR, fileName).toPath();
    }
}