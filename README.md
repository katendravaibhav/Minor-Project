# Eavesdropping Android Application

## Overview
This project presents a proof-of-concept (PoC) Android client that investigates security weaknesses in the Android permission model, persistent background services, and inter-process communication (IPC) channels, with a specific emphasis on Android’s integration in automotive infotainment systems.  
The implementation demonstrates the feasibility of covert audio acquisition and exfiltration, simulating an adversary capable of leveraging legitimate system APIs and granted permissions to compromise user privacy without overt indicators of malicious activity.

**Note:** This project is intended solely for academic research and controlled security testing. It must not be deployed in any environment without explicit authorization.

---

## Objectives
- Evaluate the resilience of Android’s permission enforcement mechanisms against abuse by over-privileged applications.
- Examine the security implications of long-lived, automatically initiated background services in vehicular contexts.
- Demonstrate a realistic attack chain involving covert data capture, local persistence, and opportunistic network-based exfiltration.
- Provide actionable recommendations for mitigating the demonstrated attack vectors.

---

## Implementation Summary

### Approach 1 – Disguised Media Player
- Implements a functional media playback interface to establish user trust.
- Embeds a concealed audio capture process operating concurrently with legitimate playback.
- Operates offline for extended periods, persisting captured audio locally and synchronizing when network connectivity becomes available.
- Utilizes legitimate Android `MediaRecorder` API calls to avoid triggering system-level anomaly detection.

### Approach 2 – Remote Audio Capture Client
- Headless Android service (no visible activity or launcher icon).
- Automatically registered via `BOOT_COMPLETED` broadcast receiver to initiate at system startup.
- Maintains a persistent TCP socket connection to a command-and-control (C2) server.
- Responds to authenticated trigger commands (e.g., `"mic"`) by initiating targeted audio capture sessions.
- Transmits audio payloads as byte streams to the C2 endpoint over the established channel.

---

## System Architecture

1. **Socket Service**  
   - Implements a persistent C2 communication channel.
   - Utilizes blocking I/O to maintain low CPU utilization while idle.
   - Supports command parsing for initiating capture operations.

2. **Audio Recording Module**  
   - Leverages `MediaRecorder` for PCM/AMR audio capture at configurable durations.
   - Implements timed stop conditions to minimize forensic traces.
   - Stores payloads temporarily in device storage (optional), with automatic cleanup after successful exfiltration.

3. **Data Transmission**  
   - Encodes audio into byte arrays for transport efficiency.
   - Supports opportunistic retransmission in environments with intermittent connectivity.

---

## Security Analysis

- **Permission Abuse**  
  Demonstrates how over-granted privileges (`RECORD_AUDIO`, `INTERNET`, `WRITE_EXTERNAL_STORAGE`, `READ_EXTERNAL_STORAGE`) can be repurposed for malicious objectives.

- **Background Service Persistence**  
  Shows how autostart capabilities and persistent services can maintain attacker presence across reboots without user re-engagement.

- **Insecure IPC and C2 Channels**  
  Highlights risks of unencrypted control channels that may be susceptible to interception, replay attacks, or command injection.

---

## Potential Impacts
- Covert acquisition of high-value voice data for social engineering, identity theft, or competitive intelligence.
- Data leakage from sensitive in-vehicle conversations to external, untrusted endpoints.
- Establishment of a foothold for broader device compromise and lateral movement.

---

## Recommended Mitigations
- **Least Privilege Enforcement**: Enforce granular runtime permission granting and revoke unused privileges dynamically.
- **Service Execution Hardening**: Restrict background execution privileges for applications lacking active foreground components.
- **Secure Channel Protocols**: Implement mutual authentication and end-to-end encryption for all IPC and network communication.
- **User Awareness Mechanisms**: Introduce mandatory visual or audible cues during microphone access.

---

## Future Research Directions
- Exploration of Android framework vulnerabilities that allow privilege escalation or security control bypass.
- Development of secure offline data handling mechanisms with delayed, authenticated transmission.
- Investigation into steganographic techniques for concealing exfiltrated data within benign transport mediums.
- Analysis of SELinux policy enforcement and its efficacy in automotive-grade Android deployments.

---

## Author
**Vaibhav Katendra**  
