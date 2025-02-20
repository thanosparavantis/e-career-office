package com.careeroffice.servlet;

import com.careeroffice.service.AuthService;
import com.careeroffice.service.DepartmentService;
import com.careeroffice.service.factory.ServiceEnum;
import com.careeroffice.service.factory.ServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet({"/manage_departments"})
public class SuperAdminManageDepartmentsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        AuthService authService = new AuthService(request.getSession());
        if (!authService.isLoggedIn()) {
            response.sendRedirect("login");
            return;
        }

        if (!authService.hasRole("super_admin")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        DepartmentService departmentService = (DepartmentService) ServiceFactory.getService(ServiceEnum.DepartmentService);

        request.setAttribute("user", authService.getUser());
        request.setAttribute("departments", departmentService.findAll());
        request.setAttribute("departmentCount", departmentService.count());

        request.getRequestDispatcher("WEB-INF/views/super_admin/manage_departments.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }
}
