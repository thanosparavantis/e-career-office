<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Super Admin | Edit Keyword</title>
    <meta charset="UTF-8">
    <c:import url="/WEB-INF/views/styles.jsp"></c:import>
</head>
<body class="fixed-nav sticky-footer bg-dark">
<c:import url="/WEB-INF/views/nav.jsp"></c:import>

<div class="content-wrapper">
    <div class="container-fluid">
        <ol class="breadcrumb">
            <li class="breadcrumb-item">
                <a href="<c:url value="/super_admin"/>">
                    Super Admin
                </a>
            </li>
            <li class="breadcrumb-item">
                <a href="<c:url value="/manage_keywords"/>">
                    Keywords
                </a>
            </li>
            <li class="breadcrumb-item active">
                Edit Keyword
            </li>
        </ol>
        <div class="card mb-3">
            <div class="card-header d-flex flex-row align-items-center">
                <i class="fa fa-tags mr-2"></i>
                <strong>Edit Keyword</strong>
            </div>
            <div class="card-body">
                <form action="<c:url value="/edit_keyword?id=${keyword.id}"/>" method="POST" id="edit_keyword_form">
                    <div class="form-group">
                        <strong><label for="title">Title</label></strong>
                        <input class="form-control" type="text" name="title" id="title" value="${keyword.title}" required>
                    </div>
                    <div class="form-group">
                        <strong><label for="slug">Slug</label></strong>
                        <input class="form-control" type="text" name="slug" id="slug" value="${keyword.slug}" required>
                    </div>
                </form>
            </div>
            <div class="card-footer flex-row align-items-center text-right">
                <a href="manage_keywords" class="btn btn-warning"><i class="fas fa-caret-left" style="margin-right:8px"></i>Cancel</a>
                <button type="submit" class="btn btn-success" form="edit_keyword_form"><i class="far fa-save" style="margin-right:8px"></i>Update</button>
            </div>

        </div>
        <c:import url="/WEB-INF/views/footer.jsp"></c:import>
    </div>
    <c:import url="/WEB-INF/views/scripts.jsp"></c:import>
</div>
</form>
</body>
</html>
