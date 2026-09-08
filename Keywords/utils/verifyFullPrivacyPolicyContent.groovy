package utils

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.util.KeywordUtil

import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject


class VerifyPrivacyPolicy {

	@Keyword
	def verifyFullPrivacyPolicyContent() {

		KeywordUtil.logInfo(
			"\n============================================================\n" +
			"STARTING PRIVACY POLICY CONTENT VERIFICATION\n" +
			"============================================================"
		)

		// ---------------------------------------------------------
		// Get actual Privacy Policy content from the page
		// ---------------------------------------------------------
		String actualContent = WebUI.getText(
			findTestObject(
				'Object Repository/EVVA/Footer/Privacy Policy Page'
			)
		)

		// Normalize actual content
		actualContent = normalizeText(actualContent)

		// ---------------------------------------------------------
		// Expected Privacy Policy content
		// ---------------------------------------------------------
		List<String> expectedContent = [

			'EVAA.AI PRIVACY POLICY',

			'Effective Date: May 12, 2026',

			'Last Updated: May 18, 2026',

			'1. Introduction',

			'Evaa.ai, LLC operates the EVAA.AI platform ( “Service”), an AI-enabled platform designed to support administrative and workflow functions for ophthalmology and optometry practices.',

			'This Privacy Policy explains how we collect, use, disclose, and safeguard information when you use EVAA.AI.',

			'2. Information We Collect',

			'2.1 Personal Information',

			'We may collect:',

			'Name, email, phone number',

			'Organization/practice details',

			'Login credentials',

			'2.2 Healthcare and Operational Data',

			'Depending on usage, the EVAA.AI platform may process:',

			'Patient demographic information',

			'Appointment and workflow data',

			'Clinical documentation (non-diagnostic support)',

			'The EVAA.AI platform is designed to support HIPAA-aligned workflows but customers are responsible for ensuring lawful use of PHI.',

			'2.3 Automatically Collected Data',

			'Device information',

			'IP address',

			'Usage logs',

			'App performance data',

			'3. How We Use Information',

			'We use data to:',

			'Provide and improve the EVAA.AI platform',

			'Enable workflows and automation',

			'Support customer operations',

			'Ensure system security and integrity',

			'Comply with legal obligations',

			'4. AI Processing Disclosure',

			'The EVAA.AI platform uses artificial intelligence to generate outputs (e.g., documentation, summaries, communication drafts).',

			'Outputs may be inaccurate or incomplete',

			'Human review is required',

			'Data may be processed using automated systems',

			'5. Data Sharing and Disclosure',

			'We may share information with:',

			'Service providers (hosting, infrastructure, analytics)',

			'Authorized users within your organization',

			'Legal authorities when required',

			'We do not sell personal data.',

			'6. Data Security',

			'We implement reasonable administrative, technical, and physical safeguards to protect data. However:',

			'No system is completely secure. Users are responsible for appropriate use and safeguards.',

			'7. HIPAA and Healthcare Data',

			'The EVAA.AI platform supports secure handling of healthcare data',

			'A Business Associate Agreement (BAA) may be required where applicable',

			'Customers are responsible for HIPAA compliance',

			'8. International Data Transfers',

			'If you are located outside the United States:',

			'Data may be processed in the U.S. or other jurisdictions',

			'Users are responsible for compliance with local laws (e.g., GDPR, PIPEDA)',

			'9. Data Retention',

			'We retain data:',

			'As long as necessary to provide our services, including but not limited to the EVAA.AI platform',

			'As required by law or contractual obligations',

			'10. User Rights',

			'Depending on jurisdiction, you may have rights to:',

			'Access your data',

			'Correct inaccuracies',

			'Request deletion',

			'Restrict processing',

			'Requests can be submitted to: support@evaa.ai',

			'11. Children’s Privacy',

			'The EVAA.AI platform is not intended for use by individuals under 18.',

			'12. Changes to This Policy',

			'We may update this Privacy Policy periodically. Continued use of the EVAA.AI platform constitutes acceptance.',

			'13. Contact',

			'Evaa.ai, LLC',

			'Email: support@evaa.ai',

			'Website: evaaaidev.wpenginepowered.com'
		]


		int mismatchCount = 0


		// ---------------------------------------------------------
		// Compare expected content with actual content
		// ---------------------------------------------------------
		for (int index = 0; index < expectedContent.size(); index++) {

			String expected = normalizeText(
				expectedContent[index]
			)


			// -----------------------------------------------------
			// Normal PASS
			// -----------------------------------------------------
			if (actualContent.contains(expected)) {

				KeywordUtil.logInfo(
					"PASS [" +
					(index + 1) +
					"]: " +
					expected
				)

				continue
			}


			// -----------------------------------------------------
			// MISMATCH
			// -----------------------------------------------------
			mismatchCount++


			// Find only the relevant actual text
			String actualMismatch = findActualMismatch(
				expected,
				actualContent
			)


			// Create clean word-level difference
			String difference = createWordDiff(
				expected,
				actualMismatch
			)


			KeywordUtil.logInfo(
				"\n============================================================\n" +
				"❌ FAIL [" +
				(index + 1) +
				"]: Expected content not found\n" +
				"============================================================\n\n" +

				"EXPECTED:\n\n" +
				expected +
				"\n\n" +

				"ACTUAL:\n\n" +
				actualMismatch +
				"\n\n" +

				"DIFFERENCE:\n\n" +
				difference +
				"\n\n" +

				"============================================================"
			)
		}


		// ---------------------------------------------------------
		// Final Summary
		// ---------------------------------------------------------
		KeywordUtil.logInfo(
			"\n============================================================\n" +
			"PRIVACY POLICY VERIFICATION SUMMARY\n" +
			"============================================================\n" +
			"Total Expected Items : " +
			expectedContent.size() +
			"\n" +
			"Mismatches           : " +
			mismatchCount +
			"\n" +
			"============================================================"
		)


		// ---------------------------------------------------------
		// Final Pass / Fail
		// ---------------------------------------------------------
		if (mismatchCount > 0) {

			KeywordUtil.markFailed(
				"Privacy Policy verification failed. " +
				mismatchCount +
				" content mismatch(es) found."
			)

		} else {

			KeywordUtil.markPassed(
				"Privacy Policy verification passed. " +
				"All expected content was found."
			)
		}
	}


	// =============================================================
	// Normalize text
	// =============================================================
	private String normalizeText(String text) {

		if (text == null) {
			return ''
		}

		return text
			.replace('\u00A0', ' ')
			.replace('\\@', '@')
			.replaceAll(/\s+/, ' ')
			.trim()
	}


	// =============================================================
	// Find the actual text relevant to the expected text
	//
	// IMPORTANT:
	// Does NOT add arbitrary extra words after the expected text.
	// This prevents:
	//
	// Patient demographic
	// Appointment and workflow
	//
	// from being reported as differences.
	// =============================================================
	private String findActualMismatch(
		String expected,
		String actual
	) {

		List<String> expectedWords =
			expected.split(/\s+/) as List

		List<String> actualWords =
			actual.split(/\s+/) as List


		if (expectedWords.isEmpty()) {
			return ''
		}


		// ---------------------------------------------------------
		// Find the first expected word
		// ---------------------------------------------------------
		int startIndex = -1

		for (int i = 0; i < actualWords.size(); i++) {

			if (
				actualWords[i]
					.equals(expectedWords[0])
			) {

				startIndex = i

				break
			}
		}


		// ---------------------------------------------------------
		// Expected starting word was not found
		// ---------------------------------------------------------
		if (startIndex == -1) {

			return expected
		}


		// ---------------------------------------------------------
		// Build actual comparison text
		//
		// Continue until ALL expected words have matched.
		//
		// Example:
		//
		// EXPECTED:
		// Depending on usage, the EVAA.AI platform may process:
		//
		// ACTUAL:
		// Depending on usage, the the EVAA.AI platform may process:
		// Patient demographic information
		//
		// Result:
		//
		// Depending on usage, the the EVAA.AI platform may process:
		// ---------------------------------------------------------
		List<String> result = []

		int expectedIndex = 0
		int actualIndex = startIndex


		while (
			expectedIndex < expectedWords.size() &&
			actualIndex < actualWords.size()
		) {

			result.add(
				actualWords[actualIndex]
			)


			if (
				expectedWords[expectedIndex]
					.equals(actualWords[actualIndex])
			) {

				expectedIndex++
			}


			actualIndex++
		}


		return result.join(' ')
	}


	// =============================================================
	// Create insertion-aware word difference
	// =============================================================
	private String createWordDiff(
		String expected,
		String actual
	) {

		List<String> expectedWords =
			expected.split(/\s+/) as List

		List<String> actualWords =
			actual.split(/\s+/) as List


		StringBuilder diff =
			new StringBuilder()


		int i = 0
		int j = 0


		while (
			i < expectedWords.size() &&
			j < actualWords.size()
		) {


			// -----------------------------------------------------
			// Words match
			// -----------------------------------------------------
			if (
				expectedWords[i]
					.equals(actualWords[j])
			) {

				i++
				j++

				continue
			}


			// -----------------------------------------------------
			// EXTRA WORD IN ACTUAL
			//
			// Example:
			//
			// Expected:
			// the EVAA.AI
			//
			// Actual:
			// the the EVAA.AI
			//
			// Detects:
			// EXTRA WORD IN ACTUAL: 'the'
			// -----------------------------------------------------
			if (
				j + 1 < actualWords.size() &&
				expectedWords[i]
					.equals(actualWords[j + 1])
			) {

				diff.append(
					"EXTRA WORD IN ACTUAL: '" +
					actualWords[j] +
					"'\n"
				)

				j++

				continue
			}


			// -----------------------------------------------------
			// MISSING WORD IN ACTUAL
			// -----------------------------------------------------
			if (
				i + 1 < expectedWords.size() &&
				expectedWords[i + 1]
					.equals(actualWords[j])
			) {

				diff.append(
					"MISSING WORD IN ACTUAL: '" +
					expectedWords[i] +
					"'\n"
				)

				i++

				continue
			}


			// -----------------------------------------------------
			// Different word
			// -----------------------------------------------------
			diff.append(
				"EXPECTED: '" +
				expectedWords[i] +
				"' | ACTUAL: '" +
				actualWords[j] +
				"'\n"
			)

			i++
			j++
		}


		// ---------------------------------------------------------
		// Remaining expected words = missing
		// ---------------------------------------------------------
		while (
			i < expectedWords.size()
		) {

			diff.append(
				"MISSING WORD IN ACTUAL: '" +
				expectedWords[i] +
				"'\n"
			)

			i++
		}


		// ---------------------------------------------------------
		// Remaining actual words = extra
		// ---------------------------------------------------------
		while (
			j < actualWords.size()
		) {

			diff.append(
				"EXTRA WORD IN ACTUAL: '" +
				actualWords[j] +
				"'\n"
			)

			j++
		}


		return diff.toString().trim()
	}
}