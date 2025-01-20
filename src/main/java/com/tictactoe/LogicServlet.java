package com.tictactoe;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession currentSession = req.getSession();
        Field field = extractField(currentSession);
        int index = getSelectedIndex(req);
        Sign currentSign = field.getField().get(index);
        if (Sign.EMPTY != currentSign) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/index.jsp");
            dispatcher.forward(req, resp);
            return;
        }
        field.getField().put(index, Sign.CROSS);
        if (checkWinner(resp,currentSession,field)) {
            return;
        }
        int emptyFieldIndex = field.getEmptyFieldIndex();
        if (emptyFieldIndex >= 0) {
            field.getField().put(emptyFieldIndex, Sign.NOUGHT);
            if (checkWinner(resp,currentSession,field)){
                return;
            }
        }


        List<Sign> data = field.getFieldData();
        currentSession.setAttribute("data", data);
        currentSession.setAttribute("field", field);

        resp.sendRedirect("/index.jsp");
    }
    private boolean checkWinner(HttpServletResponse response, HttpSession currentSession, Field field) throws IOException {
        Sign winner = field.checkWin();
        if (Sign.NOUGHT==winner || Sign.CROSS == winner){
            currentSession.setAttribute("winner", winner);
            List<Sign> data = field.getFieldData();
            currentSession.setAttribute("data",data);
            response.sendRedirect("/index.jsp");
            return true;
        }
        return false;
    }
    private int getSelectedIndex(HttpServletRequest request) {
        String click = request.getParameter("click");
        boolean isNumeric = click.chars().allMatch(Character::isDigit);
        return isNumeric ? Integer.parseInt(click) : 0;
    }

    private Field extractField(HttpSession currentSession) {
        Object fieldMap = currentSession.getAttribute("field");
        if (Field.class != fieldMap.getClass()) {
            currentSession.invalidate();
            throw new RuntimeException("Session is  broken,try one more time");
        }
        return (Field) fieldMap;
    }

}
