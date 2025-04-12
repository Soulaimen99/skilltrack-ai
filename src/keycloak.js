import Keycloak from 'keycloak-js';

const keycloak = new Keycloak({
    url: 'http://localhost:8080',
    realm: 'skilltrack',
    clientId: 'skilltrack-frontend',
  });

export default keycloak;
