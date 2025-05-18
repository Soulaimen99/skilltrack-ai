import React from "react";
import ReactDOM from "react-dom/client";
import {BrowserRouter} from "react-router-dom";
import {ReactKeycloakProvider} from "@react-keycloak/web";
import keycloak from "./keycloak";
import App from "./App";
import "./index.css";

console.log( "Application initializing..." );

ReactDOM.createRoot( document.getElementById( "root" ) ).render(
	<ReactKeycloakProvider
		authClient={keycloak}
		initOptions={{
			onLoad: "login-required",
			checkLoginIframe: false,
			pkceMethod: "S256",
		}}
		autoRefreshToken={true}
		persistTokens={true}
	>
		<BrowserRouter>
			<App/>
		</BrowserRouter>
	</ReactKeycloakProvider>
);
