<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="WEB-INF/pagefragments/header.jsp"/>

<div class="container-fluid">
    <div class="list-form-container">
        <div align="center">
            <a class="btn btn-primary" href="index.jsp">back to upload</a>
        </div>
        <c:forEach items="${fileNames}" var="fileName">
            <div class="custom-image-container">
                <a href="/uploads/uploaded/${fileName}">
                    <img src="/uploads/thumbs/${fileName}" height="85px">
                </a>
                <p>${fileName}</p>
            </div>
        </c:forEach>
    </div>

    <jsp:include page="WEB-INF/pagefragments/footer.jsp"/>
</div>

