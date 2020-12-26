<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity3">

<head>
    <title>Типичный Бот-Неудачник</title>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no" />
    <meta name="verification" content="028c8b3942019a6d057ee5283004d8" />
    <link rel="stylesheet" href="<c:url value="/assets/css/main.css"/>" />

    <noscript><link rel="stylesheet" href="<c:url value="/assets/css/noscript.css"/>"/></noscript>
</head>
<body>

<!-- Header -->
<header id="header">
    <h1>Типичный Бот-Неудачник</h1>
    <nav>
        <ul>
            <li><a href="#intro">Главная</a></li>
            <li><a href="#one">Что я могу</a></li>
        </ul>
    </nav>
</header>
<!-- Intro -->
<section id="intro" class="main style1 dark fullscreen">
    <div class="content">
        <header>
            <h3>добро пожаловать!</h3>
        </header>
        <p>Типичный Бот-Неудачник это
            <strong><a href="https://vk.com/warface_gif">бот Вконтакте,</a></strong> <br />
             с которым можно оригинально разыграть друга по телефону.
        </p>
        <footer>
            <a href="#one" class="button style2 down">More</a>
        </footer>
    </div>
</section>

<!-- One -->
<section id="one" class="main style2 right dark fullscreen">
    <div class="content box style2">
        <header>
            <h2>Что я могу</h2>
        </header>
        <p>
            <br>Деятельность проекта заключается в том,что пользователь сообщества может написать боту(в личное сообщение группы)
            <br>далее бот ему предоставит выбор пранка,после того как пользователь выберет пранк и укажет сотовый номер,
            <br>бот позвонит пользователю этого номера и прочитает текст пранка.
            <br>После завершения звонка бот отправит клиенту голосовое сообщение
            <br>где будет содержатся телефонный разговор бота с человеком чей номер был указан . Бот пранкер №1.
        </p>
    </div>
    <a href="#" class="button style2 down anchored">Next</a>
</section>

<!-- Footer -->
<footer id="footer">

    <ul class="menu">
        <li>&copy; Типичный Бот-Неудачник</li>
        <li><a href="https://vk.com/warface_gif">Группа VK</a></li>
        <li><a href="https://vk.com/topic-186088523_46641467">пользовательское соглашение</a></li>
        <li><a href="https://vk.com/topic-186088523_46641470">политика возвратов денежных средств</a></li>
        <li><a href="https://vk.com/topic-186088523_46641472">политика конфиденциальности</a></li>
    </ul>

</footer>

<!-- Scripts -->
<script src="<c:url value="/assets/js/jquery.min.js"/>"></script>
<script src="<c:url value="/assets/js/jquery.poptrox.min.js"/>"></script>
<script src="<c:url value="/assets/js/jquery.scrolly.min.js"/>"></script>
<script src="<c:url value="/assets/js/jquery.scrollex.min.js"/>"></script>
<script src="<c:url value="/assets/js/browser.min.js"/>"></script>
<script src="<c:url value="/assets/js/breakpoints.min.js"/>"></script>
<script src="<c:url value="/assets/js/util.js"/>"></script>
<script src="<c:url value="/assets/js/main.js"/>"></script>

</body>
</html>