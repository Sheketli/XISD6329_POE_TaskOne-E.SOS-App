# XISD6329_POE_TaskOne-E.SOS-App

# E.SOS App

**Emergency SOS Services Project** — developed in partnership with **POWA (People Opposing Women Abuse)**, a South African non-profit organisation established in 1979 that provides free counselling, legal services, shelter, advocacy, and public awareness programmes for survivors of gender-based violence.

![Status](https://img.shields.io/badge/status-in%20development-orange)
![Platform](https://img.shields.io/badge/platform-Android%20%7C%20iOS%20%7C%20Web-blue)
![Compliance](https://img.shields.io/badge/compliance-POPIA-green)

## About

The E.SOS App is a dual-platform emergency support system consisting of a **mobile application** (Android & iOS) and a **web-based operator dashboard**. It is designed to give survivors of gender-based violence, domestic abuse, harassment, and emotional distress immediate, discreet access to emergency assistance, while giving POWA staff the tools to monitor alerts, manage cases, and coordinate response in real time.

The project is built around three core objectives:
- Help women who are currently in need connect directly with emergency services.
- Establish direct connection with emergency hotlines and support teams.
- Ensure inclusivity through an easy-to-navigate design that accommodates people of all abilities, including those with disabilities.

## Key Features

| Feature | Description |
|---|---|
| One-Tap SOS Alert | Instantly sends an emergency alert with a single tap. |
| Silent Alert | Discreetly requests help without audible, visual, or vibrational output that could alert an abuser. |
| Real-Time GPS Location Sharing | Automatically shares live location with emergency contacts and POWA operators. |
| Automated Emergency Notifications | Delivers push, SMS (Twilio), and email alerts within seconds of activation. |
| Incident Reporting | Structured, encrypted incident reports with photo/voice/text evidence upload. |
| Secure Messaging | End-to-end encrypted communication between survivors and POWA counsellors. |
| Operator Web Dashboard | Real-time monitoring, case management, and incident tracking for POWA staff. |
| Emergency Hotline Directory | Categorised directory of SAPS, ambulance, GBV, and legal aid contact numbers. |
| Offline SOS (SMS Fallback) | Sends SOS alerts via SMS when there is no internet connectivity. |
| Multi-Language Support | Supports English and Zulu, with more languages planned. |
| Awareness Resources | Educational content on abuse prevention, survivor rights, and recovery. |

## System Architecture

The E.SOS App uses a **three-tier Model-View-Controller (MVC) architecture** hosted on cloud infrastructure:

| Tier | Components | Description |
|---|---|---|
| Presentation Layer (View) | Kotlin (Android Studio) mobile app, React.js web dashboard | User-facing interfaces for survivors, operators, and admins. |
| Application Layer (Controller) | Node.js / Express.js REST API, JWT auth, Firebase Cloud Messaging, Twilio | Business logic, authentication, alert processing, notifications. |
| Data Layer (Model) | Firebase Firestore / PostgreSQL, AWS S3, Redis | Secure storage of users, alerts, reports, GPS logs, media, and caching. |

## Technology Stack

| Component | Technology |
|---|---|
| Mobile App | Kotlin (developed in Android Studio) |
| Web Dashboard | React.js |
| Backend API | Node.js + Express.js |
| Primary Database | Firebase Firestore (real-time NoSQL) |
| Relational Database | PostgreSQL (structured/audit data) |
| Media Storage | AWS S3 |
| Push Notifications | Firebase Cloud Messaging |
| SMS Gateway | Twilio |
| Location Services | Google Maps Platform |
| Authentication | JWT + Firebase Auth |
| Containerisation | Docker + Kubernetes |
| Cloud Hosting | AWS Elastic Beanstalk / Azure App Services |

## Security & Compliance

- **TLS 1.3** for all data in transit.
- **AES-256** encryption for all sensitive data at rest.
- **JWT** authentication with short expiry and refresh token rotation.
- **Role-Based Access Control (RBAC)** for system access.
- Input validation and parameterised queries to prevent SQL injection / XSS.
- API rate limiting and DDoS protection.
- Regular penetration testing and vulnerability assessments.
- Full compliance with the **Protection of Personal Information Act (POPIA), 2013**, including data minimisation, consent management, and data deletion rights.
- **WCAG 2.1 Level AA** accessibility compliance (screen reader support, scalable fonts, colour contrast, 44x44px touch targets).

## System Actors

- **Survivor / User** — primary mobile app user triggering alerts and accessing support.
- **POWA Operator** — monitors alerts and manages cases via the web dashboard.
- **Emergency Responder** (SAPS/EMS) — receives location-based alerts for physical response.
- **System Administrator** — manages accounts, configuration, and infrastructure.

## Core Use Cases

- Register Account / Login
- Trigger SOS Alert / Send Silent Alert
- Share GPS Location (automated on alert trigger)
- Submit Incident Report
- View and Manage Alerts (operator)
- Secure Messaging
- Access Emergency Directory
- Manage User Accounts (admin)
- Respond to Emergency (responder)
- Access Awareness Resources

## Mobile Application

The mobile app ("POWA HELP") is the primary survivor-facing interface, built natively in **Kotlin** using **Android Studio**, targeting Android 10+. It is designed for fast, low-navigation interaction under high stress, with the SOS button accessible from the home screen without scrolling.

**Core screens and features:**
- **Home Screen** — large **Panic Button** and **Find Shelter** button front and centre.
- **One-Tap SOS** — a single tap captures GPS coordinates and dispatches alerts to POWA operators within ~3 seconds.
- **Silent Alert** — triggered via a pre-configured, disguised sequence (e.g. volume button pattern) with zero audio, vibration, or visual confirmation on the device, so it can be used covertly in the presence of an abuser.
- **Emergency Contacts** — categorised, one-tap-to-call directory (SAPS Emergency, Family Violence Unit, GBV Command Centre, Childline SA, Legal Aid SA, Human Rights Commission).
- **Secure Incident Reporting** — a form for incident type, description, and optional name, submitted with end-to-end encryption; anonymous reporting supported.
- **Resources** — "Know Your Rights," "Signs of Abuse," "Legal Aid," and "Safe Shelters" sections with educational content.
- **Offline SOS (SMS Fallback)** — automatically switches to SMS via Twilio when there's no internet connectivity.
- **Accessibility** — WCAG 2.1 AA compliant: 44x44px minimum touch targets, 4.5:1 colour contrast, scalable text (16sp minimum), and support for TalkBack / VoiceOver screen readers.

## Web Application (Operator Dashboard & Public Site)

The web platform, built with **React.js**, serves two audiences: the public-facing POWA site for visitors/survivors seeking help, and the secured operator dashboard for POWA staff managing live cases.

**Public-facing pages:**
- **Landing Page** — "You Are Not Alone" headline with "Get Help Now" / "Our Mission" calls to action and an always-visible **Quick Exit** safety button.
- **About** — "Over 40 Years of Resistance," covering POWA's history since 1979, its vision, mission, and legacy as South Africa's first shelter for abused women.
- **Get Help / Emergency Resources** — categorised emergency numbers (Police Emergency, GBV Command Centre, SAPS Family Violence Unit, Childline SA, Emergency SMS line for persons with disabilities, Legal Aid SA).
- **Report Incident** — secure incident report form (incident type, event description, optional emergency contact) with end-to-end encryption and RSA-4096, anonymous submissions supported, and an auto-generated reference number.
- **Our App** — showcases the mobile app's One-Tap SOS and Silent Alert features, with app store / Google Play download links.
- **Resources** — "Know Your Rights," "Signs of Abuse," "Legal Aid," and "Safe Shelters," under a "Knowledge Is Power" theme.
- **Donate** — "Rebuild Lives" call to action with "Donate Now" and "Volunteer" options.

**Operator Dashboard (authenticated, internal):**
- Real-time monitoring of incoming SOS and silent alerts, including GPS location on a live map.
- Case and incident management, including status updates and operator assignment.
- Secure messaging channel with survivors.
- Incident history and reporting tools, exportable as PDF/CSV.
- Role-based access, restricted to authenticated POWA operators and administrators.

## Database Overview

Core tables: `users`, `sos_alerts`, `incident_reports`, `emergency_contacts`, `messages`, `operators`, `media_attachments`, `alert_notifications`.

Design principles: data minimisation, encryption at rest, least-privilege access control, POPIA-aligned retention, and referential integrity via foreign keys.

## Project Timeline

The project runs across a **256-day (~51-week) development cycle**, from **02 March 2026** to **12 February 2027**, in six phases:

| Phase | Name | Duration |
|---|---|---|
| 1 | Project Initiation | Weeks 1–3 |
| 2 | Requirements Analysis | Weeks 4–8 |
| 3 | System Design | Weeks 9–14 |
| 4 | Development | Weeks 15–36 |
| 5 | Testing | Weeks 37–44 |
| 6 | Deployment and Closure | Weeks 45–51 |

## Budget

Estimated total project cost: **R 309,990** (rounded to R320,000 including management buffer), covering project management, mobile/web development, backend API, UI/UX design, database, cloud infrastructure, third-party APIs, security auditing, QA, training, and a 10% contingency reserve. The project is executed at zero direct cost to POWA as an academic Work Integrated Learning (WIL) initiative, with potential future funding from DSAC, the National Lottery Commission, corporate CSI programmes, and international donors.

## Project Team (Group 5 — IIE Rosebank College, WIL 3A)

| Name | Student No. | Role |
|---|---|---|
| Sheketli Mochaki | ST10452756 | Lead Developer |
| Lerato Molokomme | ST1043926 | UI/UX Designer / QA Engineer |
| Mulweli Mudau | ST10358894 | Business Analyst |
| Kgaogelo Mashiane | ST10446936 | Project Manager |

## Project Documentation

- Project Charter (Assignment POE Part 1)
- Requirement Analysis & System Design Document (Assignment POE Part 2)
- Includes: WBS, Gantt & PERT charts, UML use case diagrams, class diagrams, ER diagrams, wireframes, and system reports.

## Disclaimer

This is an academic Work Integrated Learning (WIL) project developed for educational purposes in partnership with POWA. It is not currently a deployed, funded, or production system.
