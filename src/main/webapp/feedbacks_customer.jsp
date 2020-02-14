<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<fmt:setLocale value="${lang}"/>
<fmt:setBundle basename="messages"/>

<jsp:include page="/header.jsp" />
	<section>
		<h1>Feedbacks</h1>
		<c:if test = "${listFeedbacksDTOToLeave != null && !listFeedbacksDTOToLeave.isEmpty()}">
			<h3>Leave your feedback(s), please!</h3>
			<div class="schedule_cont">
                <c:if test = "${errors.containsKey('')}">
                    <h1><div class="text_alert"><strong><fmt:message key="${errors.get('')}"/></strong></div></h1>
                </c:if>
                <c:forEach var="feedbackDTO" items="${listFeedbacksDTOToLeave}">
					<form method="POST">
						<div class="form-block">
							<span class="popup_label">Date:</span>
							<span>${feedbackDTO.appointmentDate}</span>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<span class="popup_label">Time:</span>
							<span>${feedbackDTO.appointmentTime}:00</span>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<span class="popup_label">Service type:</span>
							<span><fmt:message key="${feedbackDTO.serviceType}"/></span>
						</div>

						<div class="form-block">
							<span class="popup_label">Customer:</span>
							<span>${feedbackDTO.customerName}</span>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<span class="popup_label">Master:</span>
							<span>${feedbackDTO.masterName}</span>
						</div>

						<div class="form-block">
							<span class="popup_label">How do you rate our service:</span>
							<br>
                            <c:if test = "${form.id == feedbackDTO.id && errors.containsKey('rating')}">
                                <div class="text_alert"><fmt:message key="${errors.get('rating')}"/></div>
                            </c:if>
							<div class="flex-container field">
                                <c:forEach var="i" begin="1" end="10" step="1" >
                                    <input type="radio" name="rating" class="radio-button" value="${i}" <c:if test = "${form.id == feedbackDTO.id}"><c:if test = "${form.rating == i}">checked</c:if></c:if> >${i}
                                </c:forEach>
							</div>
							<br>
							<span class="popup_label">Your text here:</span>
							<br>
							<textarea name="textMessage" class="popup_text field"><c:if test = "${form.id == feedbackDTO.id}">${form.textMessage}</c:if></textarea>
                            <c:if test = "${form.id == feedbackDTO.id && errors.containsKey('textMessage')}">
                                <div class="text_alert"><fmt:message key="${errors.get('textMessage')}"/></div>
                            </c:if>
						</div>

						<input type="hidden" name="id" class="popup_input field" value="${feedbackDTO.id}" />
						<button type="submit" class="ui-button ui-corner-all ui-widget">leave feedback</button>
					</form>
				</c:forEach>
			</div>
		</c:if>

		<c:if test = "${paginationFeedbacksDTO != null}">
		    <c:if test = "${paginationFeedbacksDTO.totalRecords > 0}">
                <div class="schedule_cont">
                    <h3>Your feedbacks</h3>
                    <table class="table_schedule">
                        <tr>
                            <th>Date</th>
                            <th>Time</th>
                            <th>Service type</th>
                            <th>Customer</th>
                            <th>Master</th>
                            <th>Rating</th>
                            <th>Text</th>
                        </tr>
                        <c:forEach var="feedbackDTO" items="${paginationFeedbacksDTO.items}">
                            <tr>
                                <td>${feedbackDTO.appointmentDate}</td>
                                <td>${feedbackDTO.appointmentTime}</td>
                                <td><fmt:message key="${feedbackDTO.serviceType}"/></td>
                                <td>${feedbackDTO.customerName}</td>
                                <td>${feedbackDTO.masterName}</td>
                                <td>${feedbackDTO.rating}</td>
                                <td>${feedbackDTO.textMessage}</td>
                            </tr>
        				</c:forEach>
                    </table>

                    <c:if test = "${paginationFeedbacksDTO.totalPages > 1}">
                        <div class="pagination">
                            <c:if test = "${paginationFeedbacksDTO.hasPrevious()}">
                                <a href="/feedbacks?page=${paginationFeedbacksDTO.page-1}">&laquo;</a>
                            </c:if>

                            <c:forEach var="i" begin="0" end="${paginationFeedbacksDTO.totalPages - 1}" step="1">
                                <c:choose>
                                    <c:when test = "${paginationFeedbacksDTO.page == i}">
                                        <a class="active" >${i+1}</a>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="/feedbacks?page=${i}">${i+1}</a>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>

                            <c:if test = "${paginationFeedbacksDTO.hasNext()}">
                                <a href="/feedbacks?page=${paginationFeedbacksDTO.page+1}">&raquo;</a>
                            </c:if>
                        </div>
                    </c:if>
                </div>
            </c:if>
		    <c:if test = "${paginationFeedbacksDTO.totalRecords == 0}">
                <div class="schedule_cont">
                    <h3>Nothing to show yet...</h3>
                </div>
            </c:if>
		</c:if>

    </section>

<jsp:include page="/footer.jsp" />