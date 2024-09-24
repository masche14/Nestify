<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="kopo.poly.dto.MailDTO" %>
<%@ page import="kopo.poly.util.CmmUtil" %>
<%
    List<MailDTO> rList = (List<MailDTO>) request.getAttribute("rList");
%>
<!DOCTYPE html>
<html>
<head>
    <title>이메일 리스트</title>
    <link rel="stylesheet" href="/css/table.css"/>
</head>
<body>
<h2>안응민</h2>
<hr/>
<br/>
<div class="divTable minimalistBlack">
    <div class="divTableHeading">
        <div class="divTableRow">
            <div class="divTableHead">순번</div>
            <div class="divTableHead">제목</div>
            <div class="divTableHead">내용</div>
            <div class="divTableHead">받는사람</div>
            <div class="divTableHead">보내는사람</div>
            <div class="divTableHead">발송시간</div>
        </div>
    </div>
    <div class="divTableBody">
        <%
            for (MailDTO dto : rList) {
        %>
        <div class="divTableRow">
            <div class="divTableCell"><%=CmmUtil.nvl(dto.getSeq())%>
            </div>
            <div class="divTableCell"><%=CmmUtil.nvl(dto.getTitle())%>
            </div>
            <div class="divTableCell"><%=CmmUtil.nvl(dto.getContents())%>
            </div>
            <div class="divTableCell"><%=CmmUtil.nvl(dto.getToMail())%>
            </div>
            <div class="divTableCell"><%=CmmUtil.nvl(dto.getFromMail())%>
            </div>
            <div class="divTableCell"><%=CmmUtil.nvl(dto.getSendDT())%>
            </div>
        </div>
        <%
            }
        %>
    </div>
</div>
</body>
</html>
