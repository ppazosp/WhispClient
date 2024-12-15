# WhispClient

WhispClient is the client implementation for **Whisp**, a peer-to-peer (P2P) instant messaging application focused on privacy and functionality. 
This project originated as part of a Distributed Computing course and has evolved into a fully functional application. 
Communication is powered by **Java RMI**, with security as a key feature.


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

## Insight

Login screen:

<img width="330" alt="Screenshot 2024-12-15 at 16 32 38" src="https://github.com/user-attachments/assets/f98b5448-9f33-4141-90a9-303949269311" />

Registration 2FA screen:

<img width="330" alt="register" src="https://github.com/user-attachments/assets/b59017ff-d729-464c-ba84-f93b7389b101" />

Menu screen:

<img width="800" alt="menu" src="https://github.com/user-attachments/assets/eecc4ce1-9a25-45f5-9fc3-d8c772ec4b3d" />


## Contributors

- [ppazosp](https://github.com/ppazosp)
- [DavidMUSC](https://github.com/DavidMUSC)


## Related repositories

- [Client](https://github.com/ppazosp/WhispClient)
- [Server](https://github.com/ppazosp/WhispServer)
