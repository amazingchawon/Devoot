package com.gamee.devoot_backend.common.enums;

public enum TagType {
	HTML("html", "HTML"),
	JAVASCRIPT("javascript", "JavaScript"),
	JAVA("java", "Java"),
	RUBY("ruby", "Ruby"),
	TYPESCRIPT("typescript", "TypeScript"),
	SWIFT("swift", "Swift"),
	KOTLIN("kotlin", "Kotlin"),
	PYTHON("python", "Python"),
	C("c", "C"),
	C_PLUS_PLUS("c++", "C++"),
	C_SHARP("c#", "C#"),
	GO("go", "Go"),
	SCALA("scala", "Scala"),
	DART("dart", "Dart"),
	MYSQL("mysql", "MySQL"),
	ORACLE("oracle", "Oracle"),
	MARKDOWN("markdown", "Markdown"),
	DATA_ANALYSIS("데이터 분석", "데이터 분석"),
	DATA_ENGINEERING("데이터 엔지니어링", "데이터 엔지니어링"),
	DEEP_LEARNING_AND_MACHINE_LEARNING("딥러닝/머신러닝", "딥러닝/머신러닝"),
	COMPUTER_VISION("컴퓨터 비전", "컴퓨터 비전"),
	NLP("자연어 처리", "자연어 처리"),
	OPERATING_SYSTEM("시스템/운영체제", "시스템/운영체제"),
	BLOCKCHAIN("블록체인", "블록체인"),
	COMPUTER_ARCHITECTURE("컴퓨터 구조", "컴퓨터 구조"),
	EMBEDDED_IOT("임베디드/iot", "임베디드/IoT"),
	SEMICONDUCTOR("반도체", "반도체"),
	ROBOTICS("로봇공학", "로봇공학"),
	UX_UI("ux/ui", "UX/UI");

	private final String canonicalName;
	private final String displayName;

	TagType(String canonicalName, String displayName) {
		this.canonicalName = canonicalName;
		this.displayName = displayName;
	}

	public String getCanonicalName() {
		return canonicalName;
	}

	public String getDisplayName() {
		return displayName;
	}
}
