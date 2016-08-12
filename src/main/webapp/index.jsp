<jsp:include page="WEB-INF/pagefragments/header.jsp"/>

<div class="container-fluid" align="center">
    <div class="upload-form-container">
        <form accept-charset="UTF-8" class="form" method="POST" enctype="multipart/form-data" action="image/">
            <div class="form-group">
                <label for="file-input"> File to upload: </label>
                <input id="file-input" class="file" type="file" name="data" accept="image/*">
            </div>
            <div class="form-group">
                <label for="file-name-at-server">File name at server: </label>
                <input placeholder="name of file" id="file-name-at-server" class="form-control" type="text"
                       name="file-name">
            </div>
            <div class="form-group">
                <input id="upload-button" type="submit" value="upload" class="btn btn-primary">
            </div>
        </form>
    </div>
</div>


<jsp:include page="WEB-INF/pagefragments/footer.jsp"/>
