package com.tictactoe;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Field field = extractField(session);
        int index = getSelectedIndex(req);
        Sign currentSign = field.getField().get(index);
        if(Sign.EMPTY != currentSign){
            RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher("/index.jsp");
            requestDispatcher.forward(req, resp);
            return;
        }
         field.getField().put(index, Sign.CROSS);
        if(checkWin(resp,session,field)){
         return;
        }
        int emptyField = field.getEmptyFieldIndex();
        if(emptyField >= 0){
            field.getField().put(emptyField,Sign.NOUGHT);
            if(checkWin(resp,session,field)){
                return;
            }
        }
        else{
            session.setAttribute("draw", true);
            List<Sign> data = field.getFieldData();
            session.setAttribute("data", data);
            resp.sendRedirect("/index.jsp");
            return;
        }


        List<Sign> data = field.getFieldData();
        session.setAttribute("data", data);
        session.setAttribute("field", field);

        resp.sendRedirect("/index.jsp");
    }


    private int getSelectedIndex(HttpServletRequest req){
        String click = req.getParameter("click");
        boolean isDigit = click.chars().allMatch(Character::isDigit);
        return isDigit? Integer.parseInt(click):0;
    }

    private Field extractField(HttpSession session){
        Object field = session.getAttribute("field");
        if(Field.class != field.getClass()){
            session.invalidate();
            throw new RuntimeException("Session is broken");
        }
        return (Field) field;
    }

    private boolean checkWin(HttpServletResponse response, HttpSession session, Field field) throws IOException {
        Sign winner = field.checkWin();
        if(Sign.CROSS == winner || Sign.NOUGHT == winner){
            session.setAttribute("winner", winner);
            List<Sign> data = field.getFieldData();
            session.setAttribute("data", data);
            response.sendRedirect("/index.jsp");
            return true;
        }
        return false;
    }
}
