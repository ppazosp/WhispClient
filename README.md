# WhispClient

WhispClient is the client implementation for **Whisp**, a peer-to-peer (P2P) instant messaging application focused on privacy and functionality. 
This project originated as part of a Distributed Computing course and has evolved into a fully functional application. 
Communication is powered by **Java RMI**, with security as a key feature.

---

## Key Features

- **Advanced Security**:
  - TLS encryption for client-server communication.
  - AES-GCM encryption for client-to-client communication.
  - Passwords stored encrypted in the database.
  - Two-factor authentication for enhanced security.

- **Client Functionality**:
  - Login/Registration system.
  - Friendship system.
  - Text/image-based chats.
  - Privacy-first design: messages are not stored and are deleted when both users disconnect.

- **P2P Communication**: Built using Java RMI for a smooth and decentralized experience.

---



## Insights

| Screen             | Description                                              | Screenshot                               |
|--------------------|----------------------------------------------------------|-----------------------------------------|
| **Login Screen**   | The login screen users see when they start the application. | <div align="center"><img src="https://github.com/user-attachments/assets/f98b5448-9f33-4141-90a9-303949269311" width="150" /></div> |
| **Registration 2FA** | The screen for two-factor authentication during registration. | <div align="center"><img src="https://github.com/user-attachments/assets/b59017ff-d729-464c-ba84-f93b7389b101" width="150" /></div> |
| **Main Screen**    | The main screen showing the application's functionalities. | <div align="center"><img src="https://github.com/user-attachments/assets/eecc4ce1-9a25-45f5-9fc3-d8c772ec4b3d" width="300" /></div> |

---

## Technologies Used

- **Language:** [Java](https://www.java.com)
- **Frontend:** [JavaFX](https://openjfx.io)
- **IDE:** [IntelliJ IDEA](https://www.jetbrains.com/es-es/idea/)

---

## Contributors

- [ppazosp](https://github.com/ppazosp)
- [DavidMUSC](https://github.com/DavidMUSC)

---

## Related repositories

- [Server](https://github.com/ppazosp/WhispServer)
