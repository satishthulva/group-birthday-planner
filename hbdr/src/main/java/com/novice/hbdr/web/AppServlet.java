/*
 * AppServlet.java
 */
package com.novice.hbdr.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Singleton;

/**
 * @author satishbabu
 */
@Singleton
public class AppServlet extends HttpServlet
{
    private static final long serialVersionUID = 2949723514392677934L;

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        System.out.println("Get called on app servlet");
        
        PrintWriter printWriter = resp.getWriter();
        printWriter.write(
                "<html>"
                        + "<head>"
                            + "<title>Welcome file HBDR</title>"
                        + "</head>"
                        + "<body>"
                            + "<h1>Weclome to HBDR app</h1>"
                        + "</body>"
                + "</html>"
                );
    }

}
